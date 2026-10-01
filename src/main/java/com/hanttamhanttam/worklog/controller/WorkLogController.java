package com.hanttamhanttam.worklog.controller;

import com.hanttamhanttam.worklog.dto.WorkLogCreateRequest;
import com.hanttamhanttam.worklog.dto.WorkLogResponse;
import com.hanttamhanttam.worklog.dto.WorkLogUpdateRequest;
import com.hanttamhanttam.worklog.service.WorkLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/work-logs")
public class WorkLogController {

    private final WorkLogService workLogService;

    @PostMapping
    public ResponseEntity<WorkLogResponse> create(
            Authentication authentication,
            @PathVariable Long projectId,
            @Valid @RequestBody WorkLogCreateRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        WorkLogResponse response =
                workLogService.create(
                        userId,
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkLogResponse>> findAll(
            Authentication authentication,
            @PathVariable Long projectId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<WorkLogResponse> response =
                workLogService.findAll(
                        userId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{workLogId}")
    public ResponseEntity<WorkLogResponse> update(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long workLogId,
            @Valid @RequestBody WorkLogUpdateRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        WorkLogResponse response =
                workLogService.update(
                        userId,
                        projectId,
                        workLogId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workLogId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long workLogId
    ) {

        Long userId = (Long) authentication.getPrincipal();

        workLogService.delete(
                userId,
                projectId,
                workLogId
        );

        return ResponseEntity.noContent().build();
    }
}
