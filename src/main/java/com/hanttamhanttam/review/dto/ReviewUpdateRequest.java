package com.hanttamhanttam.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {

    private String modifications;
    private String goodPoints;
    private String badPoints;

}
