package com.hanttamhanttam.gauge.controller;

import com.hanttamhanttam.gauge.dto.GaugeCreateRequest;
import com.hanttamhanttam.gauge.dto.GaugeResponse;
import com.hanttamhanttam.gauge.dto.GaugeUpdateRequest;
import com.hanttamhanttam.gauge.service.GaugeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/gauges")
public class GaugeController {

    private final GaugeService gaugeService;

    @PostMapping
    public ResponseEntity<GaugeResponse> create(
            Authentication authentication,
            @PathVariable Long projectId,
            @Valid @RequestBody GaugeCreateRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        GaugeResponse response =
                gaugeService.create(
                        userId,
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<GaugeResponse>> findAll(
            Authentication authentication,
            @PathVariable Long projectId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<GaugeResponse> response =
                gaugeService.findAll(
                        userId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{gaugeId}/select")
    public ResponseEntity<GaugeResponse> select(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long gaugeId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        GaugeResponse response =
                gaugeService.select(
                        userId,
                        projectId,
                        gaugeId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{gaugeId}")
    public ResponseEntity<GaugeResponse> update(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long gaugeId,
            @Valid @RequestBody GaugeUpdateRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        GaugeResponse response =
                gaugeService.update(
                        userId,
                        projectId,
                        gaugeId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{gaugeId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long gaugeId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        gaugeService.delete(
                userId,
                projectId,
                gaugeId
        );

        return ResponseEntity.noContent().build();
    }
}
