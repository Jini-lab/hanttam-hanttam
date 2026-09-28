package com.hanttamhanttam.project.dto;

import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.domain.ProjectStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ProjectResponse {

    private final Long projectId;
    private final Long patternId;

    private final String size;
    private final ProjectStatus status;

    private final Integer patternGaugeStitchCount;
    private final Integer patternGaugeRowCount;
    private final BigDecimal patternGaugeWidthCm;
    private final BigDecimal patternGaugeHeightCm;

    private final LocalDate startDate;
    private final LocalDate completedDate;

    private final Integer currentPage;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ProjectResponse(Project project) {
        this.projectId = project.getProjectId();
        this.patternId = project.getPatternId();
        this.size = project.getSize();
        this.status = project.getStatus();

        this.patternGaugeStitchCount =
                project.getPatternGaugeStitchCount();
        this.patternGaugeRowCount =
                project.getPatternGaugeRowCount();
        this.patternGaugeWidthCm =
                project.getPatternGaugeWidthCm();
        this.patternGaugeHeightCm =
                project.getPatternGaugeHeightCm();

        this.startDate = project.getStartDate();
        this.completedDate = project.getCompletedDate();
        this.currentPage = project.getCurrentPage();

        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }
}
