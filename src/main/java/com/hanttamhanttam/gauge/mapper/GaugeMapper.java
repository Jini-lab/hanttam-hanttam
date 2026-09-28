package com.hanttamhanttam.gauge.mapper;

import com.hanttamhanttam.gauge.domain.Gauge;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GaugeMapper {

    void insert(Gauge gauge);
}
