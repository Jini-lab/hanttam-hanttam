package com.hanttamhanttam.gauge.mapper;

import com.hanttamhanttam.gauge.domain.Gauge;
import com.hanttamhanttam.gauge.dto.GaugeUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GaugeMapper {

    void insert(Gauge gauge);

    List<Gauge> findAllByProjectId(
            Long projectId
    );

    Gauge findById(
            @Param("gaugeId") Long gaugeId,
            @Param("projectId") Long projectId
    );

    int clearSelected(Long projectId);

    int select(
            @Param("gaugeId") Long gaugeId,
            @Param("projectId") Long projectId
    );

    int update(
            @Param("gaugeId") Long gaugeId,
            @Param("projectId") Long projectId,
            @Param("request") GaugeUpdateRequest request
    );
}
