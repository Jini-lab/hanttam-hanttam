package com.hanttamhanttam.gauge.service;

import com.hanttamhanttam.gauge.domain.Gauge;
import com.hanttamhanttam.gauge.dto.GaugeCreateRequest;
import com.hanttamhanttam.gauge.dto.GaugeResponse;
import com.hanttamhanttam.gauge.dto.GaugeUpdateRequest;
import com.hanttamhanttam.gauge.exception.GaugeNotFoundException;
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

    @Transactional
    public GaugeResponse select(
            Long userId,
            Long projectId,
            Long gaugeId
    ) {

        // 1. Project 존재 + 소유권 확인
        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        // 2. 완성 작품은 Gauge 변경 불가
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new CompletedProjectModificationException();
        }

        // 3. Gauge가 해당 Project에 실제 존재하는지 확인
        Gauge gauge =
                gaugeMapper.findById(
                        gaugeId,
                        projectId
                );

        if (gauge == null) {
            throw new GaugeNotFoundException();
        }

        // 4. 기존 최종 Gauge 선택 해제
        gaugeMapper.clearSelected(projectId);

        // 5. 새로운 최종 Gauge 선택
        gaugeMapper.select(
                gaugeId,
                projectId
        );

        // 6. Project 최근 활동 갱신
        projectMapper.touchUpdatedAt(
                projectId,
                userId
        );

        // 7. 변경된 Gauge 다시 조회
        Gauge selectedGauge =
                gaugeMapper.findById(
                        gaugeId,
                        projectId
                );

        return new GaugeResponse(selectedGauge);
    }

    @Transactional
    public GaugeResponse update(
            Long userId,
            Long projectId,
            Long gaugeId,
            GaugeUpdateRequest request
    ) {

        // Project 존재 + 소유권 확인
        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        // 완성된 작품은 Gauge 수정 불가
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new CompletedProjectModificationException();
        }

        // 해당 Project의 Gauge인지 확인
        Gauge gauge =
                gaugeMapper.findById(
                        gaugeId,
                        projectId
                );

        if (gauge == null) {
            throw new GaugeNotFoundException();
        }

        gaugeMapper.update(
                gaugeId,
                projectId,
                request
        );

        // Gauge 수정도 Project 활동으로 간주
        projectMapper.touchUpdatedAt(
                projectId,
                userId
        );

        Gauge updatedGauge =
                gaugeMapper.findById(
                        gaugeId,
                        projectId
                );

        return new GaugeResponse(updatedGauge);
    }
}
