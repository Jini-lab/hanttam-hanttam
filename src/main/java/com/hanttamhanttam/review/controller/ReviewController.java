package com.hanttamhanttam.review.controller;

import com.hanttamhanttam.review.dto.ReviewResponse;
import com.hanttamhanttam.review.dto.ReviewUpdateRequest;
import com.hanttamhanttam.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ReviewResponse> findByProjectId(
            Authentication authentication,
            @PathVariable Long projectId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        ReviewResponse response =
                reviewService.findByProjectId(
                        userId,
                        projectId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ReviewResponse> update(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody ReviewUpdateRequest request
    ) {

        Long userId = (Long) authentication.getPrincipal();

        ReviewResponse response =
                reviewService.update(
                        userId,
                        projectId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}
