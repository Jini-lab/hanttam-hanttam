package com.hanttamhanttam.worklog.service;

import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.ProjectDetailResponse;
import com.hanttamhanttam.project.exception.InvalidProjectStatusException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.mapper.ProjectMapper;
import com.hanttamhanttam.worklog.domain.WorkLog;
import com.hanttamhanttam.worklog.dto.WorkLogCreateRequest;
import com.hanttamhanttam.worklog.dto.WorkLogResponse;
import com.hanttamhanttam.worklog.exception.InvalidWorkLogPageException;
import com.hanttamhanttam.worklog.mapper.WorkLogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkLogServiceTest {

    @Mock
    private WorkLogMapper workLogMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private WorkLogService workLogService;

    @Test
    void create_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setTotalPages(20);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        WorkLogCreateRequest request =
                new WorkLogCreateRequest();

        request.setPatternPage(7);
        request.setContent("목 부분에서 2코 줄임");

        doAnswer(invocation -> {

            WorkLog workLog =
                    invocation.getArgument(0);

            workLog.setWorkLogId(1L);

            return null;

        }).when(workLogMapper)
                .insert(any(WorkLog.class));


        // when
        WorkLogResponse result =
                workLogService.create(
                        userId,
                        projectId,
                        request
                );


        // then
        assertEquals(1L, result.getWorkLogId());
        assertEquals(projectId, result.getProjectId());
        assertEquals(7, result.getPatternPage());
        assertEquals(
                "목 부분에서 2코 줄임",
                result.getContent()
        );

        ArgumentCaptor<WorkLog> captor =
                ArgumentCaptor.forClass(WorkLog.class);

        verify(workLogMapper)
                .insert(captor.capture());

        WorkLog savedWorkLog =
                captor.getValue();

        assertEquals(projectId, savedWorkLog.getProjectId());
        assertEquals(7, savedWorkLog.getPatternPage());
        assertEquals(
                "목 부분에서 2코 줄임",
                savedWorkLog.getContent()
        );

        verify(projectMapper)
                .touchUpdatedAt(
                        projectId,
                        userId
                );
    }

    @Test
    void create_preparingProject_throwsException() {

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setStatus(ProjectStatus.PREPARING);
        project.setTotalPages(20);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        WorkLogCreateRequest request =
                new WorkLogCreateRequest();

        request.setPatternPage(7);
        request.setContent("작업 기록");

        assertThrows(
                InvalidProjectStatusException.class,
                () -> workLogService.create(
                        1L,
                        100L,
                        request
                )
        );

        verifyNoInteractions(workLogMapper);

        verify(projectMapper, never())
                .touchUpdatedAt(
                        anyLong(),
                        anyLong()
                );
    }

    @Test
    void create_exceedsTotalPages_throwsException() {

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setTotalPages(20);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        WorkLogCreateRequest request =
                new WorkLogCreateRequest();

        request.setPatternPage(21);
        request.setContent("작업 기록");

        assertThrows(
                InvalidWorkLogPageException.class,
                () -> workLogService.create(
                        1L,
                        100L,
                        request
                )
        );

        verifyNoInteractions(workLogMapper);

        verify(projectMapper, never())
                .touchUpdatedAt(
                        anyLong(),
                        anyLong()
                );
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

        WorkLog workLog1 = new WorkLog();
        workLog1.setWorkLogId(1L);
        workLog1.setProjectId(projectId);
        workLog1.setPatternPage(2);
        workLog1.setContent("몸판 시작");

        WorkLog workLog2 = new WorkLog();
        workLog2.setWorkLogId(2L);
        workLog2.setProjectId(projectId);
        workLog2.setPatternPage(4);
        workLog2.setContent("소매 분리");

        when(workLogMapper.findAllByProjectId(projectId))
                .thenReturn(
                        List.of(
                                workLog1,
                                workLog2
                        )
                );


        // when
        List<WorkLogResponse> result =
                workLogService.findAll(
                        userId,
                        projectId
                );


        // then
        assertEquals(2, result.size());

        assertEquals(
                1L,
                result.get(0).getWorkLogId()
        );

        assertEquals(
                2,
                result.get(0).getPatternPage()
        );

        assertEquals(
                "몸판 시작",
                result.get(0).getContent()
        );

        assertEquals(
                2L,
                result.get(1).getWorkLogId()
        );

        assertEquals(
                4,
                result.get(1).getPatternPage()
        );

        assertEquals(
                "소매 분리",
                result.get(1).getContent()
        );

        verify(workLogMapper)
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
                () -> workLogService.findAll(
                        1L,
                        999L
                )
        );

        verifyNoInteractions(workLogMapper);
    }

    @Test
    void findAll_completedProject_success() {

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

        when(workLogMapper.findAllByProjectId(projectId))
                .thenReturn(List.of());

        List<WorkLogResponse> result =
                workLogService.findAll(
                        userId,
                        projectId
                );

        assertTrue(result.isEmpty());

        verify(workLogMapper)
                .findAllByProjectId(projectId);
    }
}
