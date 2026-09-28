package com.hanttamhanttam.gauge.controller;

import com.hanttamhanttam.gauge.dto.GaugeCreateRequest;
import com.hanttamhanttam.gauge.dto.GaugeResponse;
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
}
