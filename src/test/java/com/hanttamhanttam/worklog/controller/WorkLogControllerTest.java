package com.hanttamhanttam.worklog.controller;

import com.hanttamhanttam.common.config.SecurityConfig;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import com.hanttamhanttam.common.security.JwtAuthenticationFilter;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.project.exception.InvalidProjectStatusException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.worklog.domain.WorkLog;
import com.hanttamhanttam.worklog.dto.WorkLogCreateRequest;
import com.hanttamhanttam.worklog.dto.WorkLogResponse;
import com.hanttamhanttam.worklog.exception.InvalidWorkLogPageException;
import com.hanttamhanttam.worklog.service.WorkLogService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(WorkLogController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
public class WorkLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkLogService workLogService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void create_success() throws Exception {

        // given
        WorkLog workLog = new WorkLog();

        workLog.setWorkLogId(1L);
        workLog.setProjectId(100L);
        workLog.setPatternPage(7);
        workLog.setContent("목 부분에서 2코 줄임");

        when(workLogService.create(
                eq(1L),
                eq(100L),
                any(WorkLogCreateRequest.class)
        )).thenReturn(
                new WorkLogResponse(workLog)
        );

        String body = """
            {
              "patternPage": 7,
              "content": "목 부분에서 2코 줄임"
            }
            """;


        // when & then
        mockMvc.perform(
                        post("/api/projects/100/work-logs")
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
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.workLogId")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.projectId")
                                .value(100L)
                )
                .andExpect(
                        jsonPath("$.patternPage")
                                .value(7)
                )
                .andExpect(
                        jsonPath("$.content")
                                .value("목 부분에서 2코 줄임")
                );

        verify(workLogService)
                .create(
                        eq(1L),
                        eq(100L),
                        any(WorkLogCreateRequest.class)
                );
    }

    @Test
    void create_invalidPatternPage_returns400()
            throws Exception {

        String body = """
            {
              "patternPage": 0,
              "content": "작업 기록"
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/work-logs")
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
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workLogService);
    }

    @Test
    void create_blankContent_returns400()
            throws Exception {

        String body = """
            {
              "patternPage": 7,
              "content": ""
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/work-logs")
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
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workLogService);
    }

    @Test
    void create_exceedsTotalPages_returns400()
            throws Exception {

        when(workLogService.create(
                eq(1L),
                eq(100L),
                any(WorkLogCreateRequest.class)
        )).thenThrow(
                new InvalidWorkLogPageException()
        );

        String body = """
            {
              "patternPage": 21,
              "content": "작업 기록"
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/work-logs")
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
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "작업 기록 페이지가 도안의 전체 페이지 범위를 벗어났습니다."
                                )
                );
    }

    @Test
    void create_projectNotFound_returns404()
            throws Exception {

        when(workLogService.create(
                eq(1L),
                eq(999L),
                any(WorkLogCreateRequest.class)
        )).thenThrow(
                new ProjectNotFoundException()
        );

        String body = """
            {
              "patternPage": 7,
              "content": "작업 기록"
            }
            """;

        mockMvc.perform(
                        post("/api/projects/999/work-logs")
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
    void create_invalidProjectStatus_returns409()
            throws Exception {

        when(workLogService.create(
                eq(1L),
                eq(100L),
                any(WorkLogCreateRequest.class)
        )).thenThrow(
                new InvalidProjectStatusException(
                        "진행 중인 프로젝트에만 작업 기록을 등록할 수 있습니다."
                )
        );

        String body = """
            {
              "patternPage": 7,
              "content": "작업 기록"
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/work-logs")
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
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "진행 중인 프로젝트에만 작업 기록을 등록할 수 있습니다."
                                )
                );
    }

    @Test
    void findAll_success() throws Exception {

        // given
        WorkLog workLog1 = new WorkLog();
        workLog1.setWorkLogId(1L);
        workLog1.setProjectId(100L);
        workLog1.setPatternPage(2);
        workLog1.setContent("몸판 시작");

        WorkLog workLog2 = new WorkLog();
        workLog2.setWorkLogId(2L);
        workLog2.setProjectId(100L);
        workLog2.setPatternPage(4);
        workLog2.setContent("소매 분리");

        when(workLogService.findAll(
                1L,
                100L
        )).thenReturn(
                List.of(
                        new WorkLogResponse(workLog1),
                        new WorkLogResponse(workLog2)
                )
        );


        // when & then
        mockMvc.perform(
                        get("/api/projects/100/work-logs")
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
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].workLogId").value(1L))
                .andExpect(jsonPath("$[0].patternPage").value(2))
                .andExpect(jsonPath("$[0].content").value("몸판 시작"))

                .andExpect(jsonPath("$[1].workLogId").value(2L))
                .andExpect(jsonPath("$[1].patternPage").value(4))
                .andExpect(jsonPath("$[1].content").value("소매 분리"));

        verify(workLogService)
                .findAll(
                        1L,
                        100L
                );
    }

    @Test
    void findAll_empty_returns200() throws Exception {

        when(workLogService.findAll(
                1L,
                100L
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/projects/100/work-logs")
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
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findAll_projectNotFound_returns404()
            throws Exception {

        when(workLogService.findAll(
                1L,
                999L
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        get("/api/projects/999/work-logs")
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
    void findAll_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/projects/100/work-logs")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(workLogService);
    }
}
