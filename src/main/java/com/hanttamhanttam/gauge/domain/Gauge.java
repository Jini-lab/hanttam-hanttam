package com.hanttamhanttam.gauge.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Gauge {

    private Long gaugeId;
    private Long projectId;

    private String yarnName;
    private BigDecimal needleSize;

    private Integer stitchCount;
    private Integer rowCount;

    private BigDecimal measuredWidthCm;
    private BigDecimal measuredHeightCm;

    private Boolean isSelected;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
