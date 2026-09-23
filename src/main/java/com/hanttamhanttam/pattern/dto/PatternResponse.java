package com.hanttamhanttam.pattern.dto;

import com.hanttamhanttam.pattern.domain.Pattern;
import lombok.Getter;

@Getter
public class PatternResponse {

    private final Long patternId;
    private final String patternName;
    private final String author;
    private final String thumbnailPath;
    private final Integer totalPages;

    public PatternResponse(Pattern pattern) {
        this.patternId = pattern.getPatternId();
        this.patternName = pattern.getPatternName();
        this.author = pattern.getAuthor();
        this.thumbnailPath = pattern.getThumbnailPath();
        this.totalPages = pattern.getTotalPages();
    }
}
