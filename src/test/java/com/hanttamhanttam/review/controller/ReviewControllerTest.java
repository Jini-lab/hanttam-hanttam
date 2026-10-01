package com.hanttamhanttam.review.controller;

import com.hanttamhanttam.common.config.SecurityConfig;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import com.hanttamhanttam.common.security.JwtAuthenticationFilter;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.gauge.controller.GaugeController;
import com.hanttamhanttam.gauge.service.GaugeService;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.review.domain.Review;
import com.hanttamhanttam.review.dto.ReviewResponse;
import com.hanttamhanttam.review.dto.ReviewUpdateRequest;
import com.hanttamhanttam.review.exception.ReviewNotFoundException;
import com.hanttamhanttam.review.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findByProjectId_success() throws Exception {

        // given
        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(100L);
        review.setModifications("소매를 5cm 짧게 뜸");
        review.setGoodPoints("핏이 마음에 듦");
        review.setBadPoints("목 부분이 조금 넓음");

        when(reviewService.findByProjectId(
                1L,
                100L
        )).thenReturn(
                new ReviewResponse(review)
        );


        // when & then
        mockMvc.perform(
                        get("/api/projects/100/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.reviewId")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.projectId")
                                .value(100L)
                )
                .andExpect(
                        jsonPath("$.modifications")
                                .value("소매를 5cm 짧게 뜸")
                )
                .andExpect(
                        jsonPath("$.goodPoints")
                                .value("핏이 마음에 듦")
                )
                .andExpect(
                        jsonPath("$.badPoints")
                                .value("목 부분이 조금 넓음")
                );

        verify(reviewService)
                .findByProjectId(
                        1L,
                        100L
                );
    }

    @Test
    void findByProjectId_emptyReview_success()
            throws Exception {

        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(100L);

        when(reviewService.findByProjectId(
                1L,
                100L
        )).thenReturn(
                new ReviewResponse(review)
        );

        mockMvc.perform(
                        get("/api/projects/100/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(10L))
                .andExpect(jsonPath("$.projectId").value(100L))
                .andExpect(jsonPath("$.modifications").isEmpty())
                .andExpect(jsonPath("$.goodPoints").isEmpty())
                .andExpect(jsonPath("$.badPoints").isEmpty());
    }

    @Test
    void findByProjectId_projectNotFound_returns404()
            throws Exception {

        when(reviewService.findByProjectId(
                1L,
                999L
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        get("/api/projects/999/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("프로젝트를 찾을 수 없습니다.")
                );
    }

    @Test
    void findByProjectId_reviewNotFound_returns404()
            throws Exception {

        when(reviewService.findByProjectId(
                1L,
                100L
        )).thenThrow(
                new ReviewNotFoundException()
        );

        mockMvc.perform(
                        get("/api/projects/100/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("리뷰를 찾을 수 없습니다.")
                );
    }

    @Test
    void findByProjectId_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/projects/100/review")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(reviewService);
    }

    @Test
    void update_success() throws Exception {

        // given
        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(100L);
        review.setModifications("소매를 5cm 짧게 뜸");
        review.setGoodPoints("핏이 마음에 듦");
        review.setBadPoints("목 부분이 조금 넓음");

        when(reviewService.update(
                eq(1L),
                eq(100L),
                any(ReviewUpdateRequest.class)
        )).thenReturn(
                new ReviewResponse(review)
        );

        String body = """
            {
              "modifications": "소매를 5cm 짧게 뜸",
              "goodPoints": "핏이 마음에 듦",
              "badPoints": "목 부분이 조금 넓음"
            }
            """;

        // when & then
        mockMvc.perform(
                        put("/api/projects/100/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(10L))
                .andExpect(jsonPath("$.projectId").value(100L))
                .andExpect(
                        jsonPath("$.modifications")
                                .value("소매를 5cm 짧게 뜸")
                )
                .andExpect(
                        jsonPath("$.goodPoints")
                                .value("핏이 마음에 듦")
                )
                .andExpect(
                        jsonPath("$.badPoints")
                                .value("목 부분이 조금 넓음")
                );

        verify(reviewService).update(
                eq(1L),
                eq(100L),
                any(ReviewUpdateRequest.class)
        );
    }

    @Test
    void update_emptyReview_success() throws Exception {

        Review review = new Review();

        review.setReviewId(10L);
        review.setProjectId(100L);

        when(reviewService.update(
                eq(1L),
                eq(100L),
                any(ReviewUpdateRequest.class)
        )).thenReturn(
                new ReviewResponse(review)
        );

        String body = """
            {
              "modifications": null,
              "goodPoints": null,
              "badPoints": null
            }
            """;

        mockMvc.perform(
                        put("/api/projects/100/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modifications").isEmpty())
                .andExpect(jsonPath("$.goodPoints").isEmpty())
                .andExpect(jsonPath("$.badPoints").isEmpty());
    }

    @Test
    void update_projectNotFound_returns404()
            throws Exception {

        when(reviewService.update(
                eq(1L),
                eq(999L),
                any(ReviewUpdateRequest.class)
        )).thenThrow(
                new ProjectNotFoundException()
        );

        String body = """
            {
              "modifications": "수정사항",
              "goodPoints": "좋았던 점",
              "badPoints": "아쉬웠던 점"
            }
            """;

        mockMvc.perform(
                        put("/api/projects/999/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("프로젝트를 찾을 수 없습니다.")
                );
    }

    @Test
    void update_reviewNotFound_returns404()
            throws Exception {

        when(reviewService.update(
                eq(1L),
                eq(100L),
                any(ReviewUpdateRequest.class)
        )).thenThrow(
                new ReviewNotFoundException()
        );

        String body = """
            {
              "modifications": null,
              "goodPoints": "핏이 마음에 듦",
              "badPoints": null
            }
            """;

        mockMvc.perform(
                        put("/api/projects/100/review")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("리뷰를 찾을 수 없습니다.")
                );
    }
}
