package com.hanttamhanttam.gauge.dto;

import com.hanttamhanttam.gauge.domain.Gauge;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class GaugeResponse {

    private final Long gaugeId;
    private final Long projectId;

    private final String yarnName;
    private final BigDecimal needleSize;

    private final Integer stitchCount;
    private final Integer rowCount;

    private final BigDecimal measuredWidthCm;
    private final BigDecimal measuredHeightCm;

    private final Boolean isSelected;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public GaugeResponse(Gauge gauge) {
        this.gaugeId = gauge.getGaugeId();
        this.projectId = gauge.getProjectId();

        this.yarnName = gauge.getYarnName();
        this.needleSize = gauge.getNeedleSize();

        this.stitchCount = gauge.getStitchCount();
        this.rowCount = gauge.getRowCount();

        this.measuredWidthCm = gauge.getMeasuredWidthCm();
        this.measuredHeightCm = gauge.getMeasuredHeightCm();

        this.isSelected = gauge.getIsSelected();

        this.createdAt = gauge.getCreatedAt();
        this.updatedAt = gauge.getUpdatedAt();

    }
}
