package com.hanttamhanttam.project.service;

import com.hanttamhanttam.pattern.domain.Pattern;
import com.hanttamhanttam.pattern.exception.PatternNotFoundException;
import com.hanttamhanttam.pattern.mapper.PatternMapper;
import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.*;
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
public class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private PatternMapper patternMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void create_success() {

        // given
        Long userId = 1L;

        ProjectCreateRequest request =
                new ProjectCreateRequest();

        request.setPatternId(10L);
        request.setSize("S");
        request.setPatternGaugeStitchCount(22);
        request.setPatternGaugeRowCount(30);
        request.setPatternGaugeWidthCm(
                new BigDecimal("10.00")
        );
        request.setPatternGaugeHeightCm(
                new BigDecimal("10.00")
        );

        Pattern pattern = new Pattern();
        pattern.setPatternId(10L);
        pattern.setUserId(userId);

        when(patternMapper.findById(10L, userId))
                .thenReturn(pattern);

        // DB의 AUTO_INCREMENT 동작 흉내
        doAnswer(invocation -> {

            Project project =
                    invocation.getArgument(0);

            project.setProjectId(100L);

            return null;

        }).when(projectMapper)
                .insert(any(Project.class));


        // when
        ProjectResponse response =
                projectService.create(
                        userId,
                        request
                );


        // then
        assertEquals(
                100L,
                response.getProjectId()
        );

        assertEquals(
                10L,
                response.getPatternId()
        );

        assertEquals(
                "S",
                response.getSize()
        );

        assertEquals(
                ProjectStatus.PREPARING,
                response.getStatus()
        );

        assertEquals(
                1,
                response.getCurrentPage()
        );

        assertNull(response.getStartDate());
        assertNull(response.getCompletedDate());

        ArgumentCaptor<Project> captor =
                ArgumentCaptor.forClass(Project.class);

        verify(projectMapper)
                .insert(captor.capture());

        Project savedProject =
                captor.getValue();

        assertEquals(
                10L,
                savedProject.getPatternId()
        );

        assertEquals(
                "S",
                savedProject.getSize()
        );

        assertEquals(
                ProjectStatus.PREPARING,
                savedProject.getStatus()
        );

        assertEquals(
                22,
                savedProject.getPatternGaugeStitchCount()
        );

        assertEquals(
                30,
                savedProject.getPatternGaugeRowCount()
        );

        assertEquals(
                new BigDecimal("10.00"),
                savedProject.getPatternGaugeWidthCm()
        );

        assertEquals(
                new BigDecimal("10.00"),
                savedProject.getPatternGaugeHeightCm()
        );

        assertEquals(
                1,
                savedProject.getCurrentPage()
        );

        assertNull(savedProject.getStartDate());
        assertNull(savedProject.getCompletedDate());
    }

    @Test
    void create_patternNotFound_throwsException() {

        // given
        Long userId = 1L;

        ProjectCreateRequest request =
                new ProjectCreateRequest();

        request.setPatternId(999L);
        request.setSize("S");

        when(patternMapper.findById(
                999L,
                userId
        )).thenReturn(null);


        // when & then
        assertThrows(
                PatternNotFoundException.class,
                () -> projectService.create(
                        userId,
                        request
                )
        );

        verify(projectMapper, never())
                .insert(any(Project.class));
    }

    @Test
    void findAll_success() {

        // given
        Long userId = 1L;

        ProjectListResponse project1 =
                new ProjectListResponse();
        project1.setProjectId(2L);
        project1.setPatternName("Ivy Top");

        ProjectListResponse project2 =
                new ProjectListResponse();
        project2.setProjectId(1L);
        project2.setPatternName("Cable Sweater");

        List<ProjectListResponse> projects =
                List.of(project1, project2);

        when(projectMapper.findAllByUserId(userId))
                .thenReturn(projects);


        // when
        List<ProjectListResponse> result =
                projectService.findAll(userId);


        // then
        assertEquals(2, result.size());

        assertEquals(
                2L,
                result.get(0).getProjectId()
        );

        assertEquals(
                "Ivy Top",
                result.get(0).getPatternName()
        );

        verify(projectMapper)
                .findAllByUserId(userId);
    }

    @Test
    void findById_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setPatternId(10L);
        project.setPatternName("Cable Sweater");
        project.setThumbnailPath("thumbnail.png");
        project.setSize("S");
        project.setStatus(ProjectStatus.PREPARING);
        project.setCurrentPage(1);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);


        // when
        ProjectDetailResponse result =
                projectService.findById(
                        userId,
                        projectId
                );


        // then
        assertEquals(
                100L,
                result.getProjectId()
        );

        assertEquals(
                "Cable Sweater",
                result.getPatternName()
        );

        assertEquals(
                "S",
                result.getSize()
        );

        assertEquals(
                ProjectStatus.PREPARING,
                result.getStatus()
        );

        verify(projectMapper)
                .findById(projectId, userId);
    }

    @Test
    void findById_notFound_throwsException() {

        // given
        when(projectMapper.findById(
                999L,
                1L
        )).thenReturn(null);


        // when & then
        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.findById(
                        1L,
                        999L
                )
        );
    }

    @Test
    void update_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse before =
                new ProjectDetailResponse();

        before.setProjectId(projectId);
        before.setSize("S");
        before.setStatus(ProjectStatus.IN_PROGRESS);

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");

        ProjectDetailResponse after =
                new ProjectDetailResponse();

        after.setProjectId(projectId);
        after.setSize("M");
        after.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectMapper.findById(
                projectId,
                userId
        ))
                .thenReturn(before)
                .thenReturn(after);

        when(projectMapper.update(
                projectId,
                userId,
                request
        )).thenReturn(1);


        // when
        ProjectDetailResponse result =
                projectService.update(
                        userId,
                        projectId,
                        request
                );


        // then
        assertEquals("M", result.getSize());

        verify(projectMapper)
                .update(
                        projectId,
                        userId,
                        request
                );

        verify(projectMapper, times(2))
                .findById(projectId, userId);
    }

    @Test
    void update_completedProject_throwsException() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.COMPLETED);

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);


        // when & then
        assertThrows(
                CompletedProjectModificationException.class,
                () -> projectService.update(
                        userId,
                        projectId,
                        request
                )
        );

        verify(projectMapper, never())
                .update(
                        anyLong(),
                        anyLong(),
                        any(ProjectUpdateRequest.class)
                );
    }

    @Test
    void update_notFound_throwsException() {

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");

        when(projectMapper.findById(
                999L,
                1L
        )).thenReturn(null);

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.update(
                        1L,
                        999L,
                        request
                )
        );

        verify(projectMapper, never())
                .update(
                        anyLong(),
                        anyLong(),
                        any(ProjectUpdateRequest.class)
                );
    }
}
