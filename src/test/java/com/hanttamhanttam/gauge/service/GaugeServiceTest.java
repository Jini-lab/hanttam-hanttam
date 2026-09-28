package com.hanttamhanttam.gauge.service;

import com.hanttamhanttam.gauge.domain.Gauge;
import com.hanttamhanttam.gauge.dto.GaugeCreateRequest;
import com.hanttamhanttam.gauge.dto.GaugeResponse;
import com.hanttamhanttam.gauge.exception.GaugeNotFoundException;
import com.hanttamhanttam.gauge.mapper.GaugeMapper;
import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.ProjectDetailResponse;
import com.hanttamhanttam.project.exception.CompletedProjectModificationException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.mapper.ProjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GaugeServiceTest {

    @Mock
    private GaugeMapper gaugeMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private GaugeService gaugeService;

    @Test
    void create_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.PREPARING);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        GaugeCreateRequest request =
                new GaugeCreateRequest();

        request.setYarnName("Merino Wool");
        request.setNeedleSize(new BigDecimal("4.50"));
        request.setStitchCount(22);
        request.setRowCount(30);
        request.setMeasuredWidthCm(new BigDecimal("10.00"));
        request.setMeasuredHeightCm(new BigDecimal("10.00"));

        doAnswer(invocation -> {

            Gauge gauge = invocation.getArgument(0);
            gauge.setGaugeId(1L);

            return null;

        }).when(gaugeMapper)
                .insert(any(Gauge.class));


        // when
        GaugeResponse result =
                gaugeService.create(
                        userId,
                        projectId,
                        request
                );


        // then
        assertEquals(1L, result.getGaugeId());
        assertEquals(projectId, result.getProjectId());
        assertEquals("Merino Wool", result.getYarnName());

        assertFalse(result.getIsSelected());

        verify(projectMapper)
                .touchUpdatedAt(
                        projectId,
                        userId
                );

        ArgumentCaptor<Gauge> captor =
                ArgumentCaptor.forClass(Gauge.class);

        verify(gaugeMapper)
                .insert(captor.capture());

        Gauge savedGauge =
                captor.getValue();

        assertEquals(
                new BigDecimal("4.50"),
                savedGauge.getNeedleSize()
        );

        assertEquals(
                22,
                savedGauge.getStitchCount()
        );

        assertEquals(
                30,
                savedGauge.getRowCount()
        );

        assertEquals(
                new BigDecimal("10.00"),
                savedGauge.getMeasuredWidthCm()
        );

        assertEquals(
                new BigDecimal("10.00"),
                savedGauge.getMeasuredHeightCm()
        );

        assertFalse(savedGauge.getIsSelected());
    }

    @Test
    void create_completedProject_throwsException() {

        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        GaugeCreateRequest request =
                new GaugeCreateRequest();

        assertThrows(
                CompletedProjectModificationException.class,
                () -> gaugeService.create(
                        userId,
                        projectId,
                        request
                )
        );

        verifyNoInteractions(gaugeMapper);

        verify(projectMapper, never())
                .touchUpdatedAt(
                        anyLong(),
                        anyLong()
                );
    }

    @Test
    void create_projectNotFound_throwsException() {

        when(projectMapper.findById(
                999L,
                1L
        )).thenReturn(null);

        GaugeCreateRequest request =
                new GaugeCreateRequest();

        assertThrows(
                ProjectNotFoundException.class,
                () -> gaugeService.create(
                        1L,
                        999L,
                        request
                )
        );

        verifyNoInteractions(gaugeMapper);
    }

    @Test
    void findAll_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        Gauge gauge1 = new Gauge();
        gauge1.setGaugeId(1L);
        gauge1.setProjectId(projectId);
        gauge1.setYarnName("Merino Wool");
        gauge1.setNeedleSize(new BigDecimal("4.50"));
        gauge1.setIsSelected(false);

        Gauge gauge2 = new Gauge();
        gauge2.setGaugeId(2L);
        gauge2.setProjectId(projectId);
        gauge2.setYarnName("Merino Wool");
        gauge2.setNeedleSize(new BigDecimal("5.00"));
        gauge2.setIsSelected(true);

        when(gaugeMapper.findAllByProjectId(projectId))
                .thenReturn(List.of(gauge1, gauge2));


        // when
        List<GaugeResponse> result =
                gaugeService.findAll(
                        userId,
                        projectId
                );


        // then
        assertEquals(2, result.size());

        assertEquals(
                1L,
                result.get(0).getGaugeId()
        );

        assertEquals(
                new BigDecimal("4.50"),
                result.get(0).getNeedleSize()
        );

        assertEquals(
                2L,
                result.get(1).getGaugeId()
        );

        assertEquals(
                new BigDecimal("5.00"),
                result.get(1).getNeedleSize()
        );

        assertTrue(
                result.get(1).getIsSelected()
        );

        verify(gaugeMapper)
                .findAllByProjectId(projectId);
    }

    @Test
    void findAll_projectNotFound_throwsException() {

        when(projectMapper.findById(
                999L,
                1L
        )).thenReturn(null);

        assertThrows(
                ProjectNotFoundException.class,
                () -> gaugeService.findAll(
                        1L,
                        999L
                )
        );

        verifyNoInteractions(gaugeMapper);
    }

    @Test
    void select_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;
        Long gaugeId = 2L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        Gauge before = new Gauge();
        before.setGaugeId(gaugeId);
        before.setProjectId(projectId);
        before.setIsSelected(false);

        Gauge after = new Gauge();
        after.setGaugeId(gaugeId);
        after.setProjectId(projectId);
        after.setIsSelected(true);

        when(gaugeMapper.findById(
                gaugeId,
                projectId
        ))
                .thenReturn(before)
                .thenReturn(after);


        // when
        GaugeResponse result =
                gaugeService.select(
                        userId,
                        projectId,
                        gaugeId
                );


        // then
        assertEquals(
                gaugeId,
                result.getGaugeId()
        );

        assertTrue(
                result.getIsSelected()
        );

        verify(gaugeMapper)
                .clearSelected(projectId);

        verify(gaugeMapper)
                .select(
                        gaugeId,
                        projectId
                );

        verify(projectMapper)
                .touchUpdatedAt(
                        projectId,
                        userId
                );

        verify(gaugeMapper, times(2))
                .findById(
                        gaugeId,
                        projectId
                );
    }

    @Test
    void select_gaugeNotFound_throwsException() {

        Long userId = 1L;
        Long projectId = 100L;
        Long gaugeId = 999L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        when(gaugeMapper.findById(
                gaugeId,
                projectId
        )).thenReturn(null);

        assertThrows(
                GaugeNotFoundException.class,
                () -> gaugeService.select(
                        userId,
                        projectId,
                        gaugeId
                )
        );

        verify(gaugeMapper, never())
                .clearSelected(anyLong());

        verify(gaugeMapper, never())
                .select(
                        anyLong(),
                        anyLong()
                );
    }

    @Test
    void select_completedProject_throwsException() {

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        assertThrows(
                CompletedProjectModificationException.class,
                () -> gaugeService.select(
                        1L,
                        100L,
                        2L
                )
        );

        verifyNoInteractions(gaugeMapper);
    }
}