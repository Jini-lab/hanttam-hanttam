package com.hanttamhanttam.project.controller;

import com.hanttamhanttam.project.dto.*;
import com.hanttamhanttam.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            Authentication authentication,
            @Valid @RequestBody ProjectCreateRequest request) {
        Long userId = (Long) authentication.getPrincipal();

        ProjectResponse response = projectService.create(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectListResponse>> findAll(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<ProjectListResponse> response =
                projectService.findAll(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> findById(
            Authentication authentication,
            @PathVariable Long projectId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        ProjectDetailResponse response =
                projectService.findById(
                        userId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> update(
            Authentication authentication,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        ProjectDetailResponse response =
                projectService.update(
                        userId,
                        projectId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/start")
    public ResponseEntity<ProjectDetailResponse> start(
            Authentication authentication,
            @PathVariable Long projectId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        ProjectDetailResponse response =
                projectService.start(
                        userId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }
}
