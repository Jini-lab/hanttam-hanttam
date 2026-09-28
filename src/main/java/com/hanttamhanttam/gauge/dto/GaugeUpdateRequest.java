package com.hanttamhanttam.gauge.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GaugeUpdateRequest {

    private String yarnName;

    @Positive(message = "바늘 크기는 0보다 커야 합니다.")
    private BigDecimal needleSize;

    @Positive(message = "코 수는 0보다 커야 합니다.")
    private Integer stitchCount;

    @Positive(message = "단 수는 0보다 커야 합니다.")
    private Integer rowCount;

    @Positive(message = "측정 너비는 0보다 커야 합니다.")
    private BigDecimal measuredWidthCm;

    @Positive(message = "측정 높이는 0보다 커야 합니다.")
    private BigDecimal measuredHeightCm;
}
