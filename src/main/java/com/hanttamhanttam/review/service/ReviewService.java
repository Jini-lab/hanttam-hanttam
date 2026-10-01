package com.hanttamhanttam.review.service;

import com.hanttamhanttam.project.dto.ProjectDetailResponse;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.mapper.ProjectMapper;
import com.hanttamhanttam.review.domain.Review;
import com.hanttamhanttam.review.dto.ReviewResponse;
import com.hanttamhanttam.review.dto.ReviewUpdateRequest;
import com.hanttamhanttam.review.exception.ReviewNotFoundException;
import com.hanttamhanttam.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public ReviewResponse findByProjectId(
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

        Review review =
                reviewMapper.findByProjectId(projectId);

        if (review == null) {
            throw new ReviewNotFoundException();
        }

        return new ReviewResponse(review);
    }

    @Transactional
    public ReviewResponse update(
            Long userId,
            Long projectId,
            ReviewUpdateRequest request
    ) {

        ProjectDetailResponse project =
                projectMapper.findById(
                        projectId,
                        userId
                );

        if (project == null) {
            throw new ProjectNotFoundException();
        }

        Review review =
                reviewMapper.findByProjectId(projectId);

        if (review == null) {
            throw new ReviewNotFoundException();
        }

        review.setModifications(
                normalize(request.getModifications())
        );

        review.setGoodPoints(
                normalize(request.getGoodPoints())
        );

        review.setBadPoints(
                normalize(request.getBadPoints())
        );

        reviewMapper.update(review);

        projectMapper.touchUpdatedAt(
                projectId,
                userId
        );

        return new ReviewResponse(review);
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }
}
