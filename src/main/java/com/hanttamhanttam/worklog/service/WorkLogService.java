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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkLogService {

    private final WorkLogMapper workLogMapper;
    private final ProjectMapper projectMapper;

    @Transactional
    public WorkLogResponse create(
            Long userId,
            Long projectId,
            WorkLogCreateRequest request
    ) {

        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        if (project.getStatus() != ProjectStatus.IN_PROGRESS) {
            throw new InvalidProjectStatusException(
                    "진행 중인 프로젝트에만 작업 기록을 등록할 수 있습니다."
            );
        }

        if (request.getPatternPage() > project.getTotalPages()) {
            throw new InvalidWorkLogPageException();
        }

        WorkLog workLog = new WorkLog();

        workLog.setProjectId(projectId);
        workLog.setPatternPage(request.getPatternPage());
        workLog.setContent(request.getContent());

        workLogMapper.insert(workLog);

        projectMapper.touchUpdatedAt(
                projectId,
                userId
        );

        return new WorkLogResponse(workLog);
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponse> findAll(
            Long userId,
            Long projectId
    ) {

        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        return workLogMapper
                .findAllByProjectId(projectId)
                .stream()
                .map(WorkLogResponse::new)
                .toList();
    }
}
