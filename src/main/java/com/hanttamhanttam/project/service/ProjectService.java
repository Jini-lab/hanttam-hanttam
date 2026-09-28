package com.hanttamhanttam.project.service;

import com.hanttamhanttam.pattern.domain.Pattern;
import com.hanttamhanttam.pattern.exception.PatternNotFoundException;
import com.hanttamhanttam.pattern.mapper.PatternMapper;
import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.*;
import com.hanttamhanttam.project.exception.CompletedProjectModificationException;
import com.hanttamhanttam.project.exception.InvalidProjectStatusException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final PatternMapper patternMapper;

    @Transactional
    public ProjectResponse create(
            Long userId,
            ProjectCreateRequest request
    ) {
        Pattern pattern = patternMapper.findById(
                request.getPatternId(),
                userId
        );

        if (pattern == null) {
            throw new PatternNotFoundException();
        }

        Project project = new Project();

        project.setPatternId(
                request.getPatternId()
        );

        project.setSize(
                request.getSize()
        );

        project.setStatus(
                ProjectStatus.PREPARING
        );

        project.setPatternGaugeStitchCount(
                request.getPatternGaugeStitchCount()
        );

        project.setPatternGaugeRowCount(
                request.getPatternGaugeRowCount()
        );

        project.setPatternGaugeWidthCm(
                request.getPatternGaugeWidthCm()
        );

        project.setPatternGaugeHeightCm(
                request.getPatternGaugeHeightCm()
        );

        project.setStartDate(null);
        project.setCompletedDate(null);

        project.setCurrentPage(1);

        projectMapper.insert(project);

        return new ProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectListResponse> findAll(
            Long userId
    ) {
        return projectMapper.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse findById(
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

        return project;
    }

    @Transactional
    public ProjectDetailResponse update(
            Long userId,
            Long projectId,
            ProjectUpdateRequest request
    ) {

        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new CompletedProjectModificationException();
        }

        projectMapper.update(
                projectId,
                userId,
                request
        );

        return projectMapper.findById(
                projectId,
                userId
        );
    }

    @Transactional
    public ProjectDetailResponse start(
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

        if (project.getStatus() != ProjectStatus.PREPARING) {
            throw new InvalidProjectStatusException(
                    "준비 중인 프로젝트만 시작할 수 있습니다."
            );
        }

        projectMapper.start(
                projectId,
                userId
        );

        return projectMapper.findById(
                projectId,
                userId
        );
    }
}
