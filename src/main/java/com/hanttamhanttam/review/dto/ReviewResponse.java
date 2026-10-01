package com.hanttamhanttam.review.dto;

import com.hanttamhanttam.review.domain.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private final Long reviewId;
    private final Long projectId;

    private final String modifications;
    private final String goodPoints;
    private final String badPoints;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReviewResponse(Review review) {
        this.reviewId = review.getReviewId();
        this.projectId = review.getProjectId();
        this.modifications = review.getModifications();
        this.goodPoints = review.getGoodPoints();
        this.badPoints = review.getBadPoints();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}
