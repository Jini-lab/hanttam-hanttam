package com.hanttamhanttam.gauge.mapper;

import com.hanttamhanttam.gauge.domain.Gauge;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GaugeMapper {

    void insert(Gauge gauge);

    List<Gauge> findAllByProjectId(
            Long projectId
    );
}
