package com.hanttamhanttam.gauge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GaugeCreateRequest {

    @NotBlank(message = "사용한 실을 입력해주세요.")
    private String yarnName;

    @NotNull(message = "바늘 크기를 입력해주세요.")
    @Positive(message = "바늘 크기는 0보다 커야 합니다.")
    private BigDecimal needleSize;

    @NotNull(message = "코 수를 입력해주세요.")
    @Positive(message = "코 수는 0보다 커야 합니다.")
    private Integer stitchCount;

    @NotNull(message = "단 수를 입력해주세요.")
    @Positive(message = "단 수는 0보다 커야 합니다.")
    private Integer rowCount;

    @NotNull(message = "측정 너비를 입력해주세요.")
    @Positive(message = "측정 너비는 0보다 커야 합니다.")
    private BigDecimal measuredWidthCm;

    @NotNull(message = "측정 높이를 입력해주세요.")
    @Positive(message = "측정 높이는 0보다 커야 합니다.")
    private BigDecimal measuredHeightCm;

}
