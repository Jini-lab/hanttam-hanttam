package com.hanttamhanttam.project.dto;

import com.hanttamhanttam.project.domain.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectListResponse {

    private Long projectId;
    private Long patternId;

    private String patternName;
    private String thumbnailPath;

    private String size;
    private ProjectStatus status;

    private Integer currentPage;

    private LocalDateTime updatedAt;
}
