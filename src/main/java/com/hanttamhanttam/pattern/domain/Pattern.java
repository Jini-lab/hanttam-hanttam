package com.hanttamhanttam.pattern.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Pattern {

    private Long patternId;
    private Long userId;
    private String patternName;
    private String author;
    private String pdfPath;
    private String thumbnailPath;
    private Integer totalPages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
