package com.hanttamhanttam.review.service;

import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.ProjectDetailResponse;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.mapper.ProjectMapper;
import com.hanttamhanttam.review.domain.Review;
import com.hanttamhanttam.review.dto.ReviewResponse;
import com.hanttamhanttam.review.dto.ReviewUpdateRequest;
import com.hanttamhanttam.review.exception.ReviewNotFoundException;
import com.hanttamhanttam.review.mapper.ReviewMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void findByProjectId_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(projectId);
        review.setModifications("소매를 5cm 짧게 뜸");
        review.setGoodPoints("핏이 마음에 듦");
        review.setBadPoints("목 부분이 조금 넓음");

        when(reviewMapper.findByProjectId(projectId))
                .thenReturn(review);


        // when
        ReviewResponse result =
                reviewService.findByProjectId(
                        userId,
                        projectId
                );


        // then
        assertEquals(10L, result.getReviewId());
        assertEquals(100L, result.getProjectId());

        assertEquals(
                "소매를 5cm 짧게 뜸",
                result.getModifications()
        );

        assertEquals(
                "핏이 마음에 듦",
                result.getGoodPoints()
        );

        assertEquals(
                "목 부분이 조금 넓음",
                result.getBadPoints()
        );

        verify(reviewMapper)
                .findByProjectId(projectId);
    }

    @Test
    void findByProjectId_projectNotFound_throwsException() {

        when(projectMapper.findById(
                999L,
                1L
        )).thenReturn(null);

        assertThrows(
                ProjectNotFoundException.class,
                () -> reviewService.findByProjectId(
                        1L,
                        999L
                )
        );

        verifyNoInteractions(reviewMapper);
    }

    @Test
    void findByProjectId_reviewNotFound_throwsException() {

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        when(reviewMapper.findByProjectId(100L))
                .thenReturn(null);

        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.findByProjectId(
                        1L,
                        100L
                )
        );
    }

    @Test
    void update_success() {

        // given
        Long userId = 1L;
        Long projectId = 100L;

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(projectId);
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectMapper.findById(
                projectId,
                userId
        )).thenReturn(project);

        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(projectId);

        when(reviewMapper.findByProjectId(projectId))
                .thenReturn(review);

        ReviewUpdateRequest request =
                new ReviewUpdateRequest();

        request.setModifications(
                "소매를 5cm 짧게 뜸"
        );

        request.setGoodPoints(
                "핏이 마음에 듦"
        );

        request.setBadPoints(
                "목 부분이 조금 넓음"
        );


        // when
        ReviewResponse result =
                reviewService.update(
                        userId,
                        projectId,
                        request
                );


        // then
        assertEquals(
                "소매를 5cm 짧게 뜸",
                result.getModifications()
        );

        assertEquals(
                "핏이 마음에 듦",
                result.getGoodPoints()
        );

        assertEquals(
                "목 부분이 조금 넓음",
                result.getBadPoints()
        );

        verify(reviewMapper)
                .update(review);

        verify(projectMapper)
                .touchUpdatedAt(
                        projectId,
                        userId
                );
    }

    @Test
    void update_nullValue_clearsExistingContent() {

        // given
        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(100L);

        review.setModifications("기존 수정사항");
        review.setGoodPoints("기존 장점");
        review.setBadPoints("기존 아쉬운 점");

        when(reviewMapper.findByProjectId(100L))
                .thenReturn(review);

        ReviewUpdateRequest request =
                new ReviewUpdateRequest();

        request.setModifications("새로운 수정사항");
        request.setGoodPoints(null);
        request.setBadPoints(null);


        // when
        ReviewResponse result =
                reviewService.update(
                        1L,
                        100L,
                        request
                );


        // then
        assertEquals(
                "새로운 수정사항",
                result.getModifications()
        );

        assertNull(result.getGoodPoints());
        assertNull(result.getBadPoints());

        verify(reviewMapper)
                .update(review);
    }

    @Test
    void update_blankValue_convertsToNull() {

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(100L);
        review.setGoodPoints("기존 내용");

        when(reviewMapper.findByProjectId(100L))
                .thenReturn(review);

        ReviewUpdateRequest request =
                new ReviewUpdateRequest();

        request.setGoodPoints("   ");


        ReviewResponse result =
                reviewService.update(
                        1L,
                        100L,
                        request
                );


        assertNull(result.getModifications());
        assertNull(result.getGoodPoints());
        assertNull(result.getBadPoints());
    }

    @Test
    void update_projectNotFound_throwsException() {

        when(projectMapper.findById(
                999L,
                1L
        )).thenReturn(null);

        ReviewUpdateRequest request =
                new ReviewUpdateRequest();

        assertThrows(
                ProjectNotFoundException.class,
                () -> reviewService.update(
                        1L,
                        999L,
                        request
                )
        );

        verifyNoInteractions(reviewMapper);
    }

    @Test
    void update_reviewNotFound_throwsException() {

        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);

        when(projectMapper.findById(
                100L,
                1L
        )).thenReturn(project);

        when(reviewMapper.findByProjectId(100L))
                .thenReturn(null);

        ReviewUpdateRequest request =
                new ReviewUpdateRequest();

        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.update(
                        1L,
                        100L,
                        request
                )
        );

        verify(reviewMapper, never())
                .update(any());
    }
}