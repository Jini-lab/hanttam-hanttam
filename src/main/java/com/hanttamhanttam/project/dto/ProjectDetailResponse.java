package com.hanttamhanttam.project.dto;

import com.hanttamhanttam.project.domain.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectDetailResponse {

    private Long projectId;
    private Long patternId;

    private String patternName;
    private String thumbnailPath;
    private Integer totalPages;

    private String size;
    private ProjectStatus status;

    private Integer patternGaugeStitchCount;
    private Integer patternGaugeRowCount;
    private BigDecimal patternGaugeWidthCm;
    private BigDecimal patternGaugeHeightCm;

    private LocalDate startDate;
    private LocalDate completedDate;

    private Integer currentPage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
