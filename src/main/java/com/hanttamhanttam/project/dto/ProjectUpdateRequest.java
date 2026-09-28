package com.hanttamhanttam.project.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProjectUpdateRequest {

    private String size;

    @Positive(message = "도안 게이지 코 수는 0보다 커야 합니다.")
    private Integer patternGaugeStitchCount;

    @Positive(message = "도안 게이지 단 수는 0보다 커야 합니다.")
    private Integer patternGaugeRowCount;

    @Positive(message = "도안 게이지 너비는 0보다 커야 합니다.")
    private BigDecimal patternGaugeWidthCm;

    @Positive(message = "도안 게이지 높이는 0보다 커야 합니다.")
    private BigDecimal patternGaugeHeightCm;

}
