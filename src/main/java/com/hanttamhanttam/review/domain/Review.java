package com.hanttamhanttam.review.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Review {

    private Long reviewId;
    private Long projectId;

    private String modifications;
    private String goodPoints;
    private String badPoints;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
