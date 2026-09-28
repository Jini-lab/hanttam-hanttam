package com.hanttamhanttam.gauge.service;

import com.hanttamhanttam.gauge.domain.Gauge;
import com.hanttamhanttam.gauge.dto.GaugeCreateRequest;
import com.hanttamhanttam.gauge.dto.GaugeResponse;
import com.hanttamhanttam.gauge.mapper.GaugeMapper;
import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.ProjectDetailResponse;
import com.hanttamhanttam.project.exception.CompletedProjectModificationException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GaugeService {

    private final GaugeMapper gaugeMapper;
    private final ProjectMapper projectMapper;

    @Transactional
    public GaugeResponse create(
            Long userId,
            Long projectId,
            GaugeCreateRequest request
    ) {

        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new CompletedProjectModificationException();
        }

        Gauge gauge = new Gauge();

        gauge.setProjectId(projectId);
        gauge.setYarnName(request.getYarnName());
        gauge.setNeedleSize(request.getNeedleSize());
        gauge.setStitchCount(request.getStitchCount());
        gauge.setRowCount(request.getRowCount());
        gauge.setMeasuredWidthCm(request.getMeasuredWidthCm());
        gauge.setMeasuredHeightCm(request.getMeasuredHeightCm());

        gauge.setIsSelected(false);

        gaugeMapper.insert(gauge);

        projectMapper.touchUpdatedAt(
                projectId,
                userId
        );

        return new GaugeResponse(gauge);
    }

    @Transactional(readOnly = true)
    public List<GaugeResponse> findAll(
            Long userId,
            Long projectId
    ) {

        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        return gaugeMapper
                .findAllByProjectId(projectId)
                .stream()
                .map(GaugeResponse::new)
                .toList();
    }
}
