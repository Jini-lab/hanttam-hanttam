package com.hanttamhanttam.project.controller;

import com.hanttamhanttam.common.config.SecurityConfig;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import com.hanttamhanttam.common.security.JwtAuthenticationFilter;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.*;
import com.hanttamhanttam.project.exception.CompletedProjectModificationException;
import com.hanttamhanttam.project.exception.InvalidCurrentPageException;
import com.hanttamhanttam.project.exception.InvalidProjectStatusException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import com.hanttamhanttam.project.service.ProjectService;
import com.hanttamhanttam.pattern.exception.PatternNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void create_success() throws Exception {

        // given
        ProjectCreateRequest request =
                new ProjectCreateRequest();

        request.setPatternId(10L);
        request.setSize("S");

        Project project = new Project();
        project.setProjectId(100L);
        project.setPatternId(10L);
        project.setSize("S");
        project.setStatus(ProjectStatus.PREPARING);
        project.setCurrentPage(1);

        when(projectService.create(
                eq(1L),
                any(ProjectCreateRequest.class)
        )).thenReturn(
                new ProjectResponse(project)
        );

        // when & then
        mockMvc.perform(
                        post("/api/projects")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.projectId").value(100L)
                )
                .andExpect(
                        jsonPath("$.patternId").value(10L)
                )
                .andExpect(
                        jsonPath("$.size").value("S")
                )
                .andExpect(
                        jsonPath("$.status").value("PREPARING")
                )
                .andExpect(
                        jsonPath("$.currentPage").value(1)
                );

        verify(projectService).create(
                eq(1L),
                any(ProjectCreateRequest.class)
        );
    }

    @Test
    void create_blankSize_returns400()
            throws Exception {

        ProjectCreateRequest request =
                new ProjectCreateRequest();

        request.setPatternId(10L);
        request.setSize("");

        mockMvc.perform(
                        post("/api/projects")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectService);
    }

    @Test
    void create_patternNotFound_returns404()
            throws Exception {

        ProjectCreateRequest request =
                new ProjectCreateRequest();

        request.setPatternId(999L);
        request.setSize("S");

        when(projectService.create(
                eq(1L),
                any(ProjectCreateRequest.class)
        )).thenThrow(
                new PatternNotFoundException()
        );

        mockMvc.perform(
                        post("/api/projects")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("도안을 찾을 수 없습니다.")
                );
    }

    @Test
    void create_withoutAuthentication_returns401()
            throws Exception {

        ProjectCreateRequest request =
                new ProjectCreateRequest();

        request.setPatternId(10L);
        request.setSize("S");

        mockMvc.perform(
                        post("/api/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }

    @Test
    void findAll_success() throws Exception {

        // given
        ProjectListResponse project =
                new ProjectListResponse();

        project.setProjectId(100L);
        project.setPatternId(10L);
        project.setPatternName("Cable Sweater");
        project.setThumbnailPath("thumbnail.png");
        project.setSize("S");
        project.setStatus(ProjectStatus.PREPARING);
        project.setCurrentPage(1);

        when(projectService.findAll(1L))
                .thenReturn(List.of(project));


        // when & then
        mockMvc.perform(
                        get("/api/projects")
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
                .andExpect(jsonPath("$[0].projectId").value(100L))
                .andExpect(jsonPath("$[0].patternId").value(10L))
                .andExpect(jsonPath("$[0].patternName").value("Cable Sweater"))
                .andExpect(jsonPath("$[0].size").value("S"))
                .andExpect(jsonPath("$[0].status").value("PREPARING"))
                .andExpect(jsonPath("$[0].currentPage").value(1));

        verify(projectService)
                .findAll(1L);
    }

    @Test
    void findAll_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/projects")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }

    @Test
    void findById_success() throws Exception {

        // given
        ProjectDetailResponse project =
                new ProjectDetailResponse();

        project.setProjectId(100L);
        project.setPatternId(10L);
        project.setPatternName("Cable Sweater");
        project.setThumbnailPath("thumbnail.png");
        project.setTotalPages(32);
        project.setSize("S");
        project.setStatus(ProjectStatus.PREPARING);
        project.setCurrentPage(1);

        when(projectService.findById(
                1L,
                100L
        )).thenReturn(project);


        // when & then
        mockMvc.perform(
                        get("/api/projects/100")
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
                        jsonPath("$.projectId")
                                .value(100L)
                )
                .andExpect(
                        jsonPath("$.patternId")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.patternName")
                                .value("Cable Sweater")
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(32)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value("S")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PREPARING")
                )
                .andExpect(
                        jsonPath("$.currentPage")
                                .value(1)
                );
    }

    @Test
    void findById_notFound_returns404()
            throws Exception {

        when(projectService.findById(
                1L,
                999L
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        get("/api/projects/999")
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
    void findById_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/projects/100")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }

    @Test
    void update_success() throws Exception {

        // given
        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");
        request.setPatternGaugeStitchCount(24);
        request.setPatternGaugeRowCount(32);

        ProjectDetailResponse response =
                new ProjectDetailResponse();

        response.setProjectId(100L);
        response.setPatternId(10L);
        response.setPatternName("Cable Sweater");
        response.setTotalPages(32);
        response.setSize("M");
        response.setStatus(ProjectStatus.IN_PROGRESS);
        response.setPatternGaugeStitchCount(24);
        response.setPatternGaugeRowCount(32);
        response.setCurrentPage(1);

        when(projectService.update(
                eq(1L),
                eq(100L),
                any(ProjectUpdateRequest.class)
        )).thenReturn(response);


        // when & then
        mockMvc.perform(
                        patch("/api/projects/100")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.projectId")
                                .value(100L)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value("M")
                )
                .andExpect(
                        jsonPath("$.patternGaugeStitchCount")
                                .value(24)
                )
                .andExpect(
                        jsonPath("$.patternGaugeRowCount")
                                .value(32)
                );

        verify(projectService).update(
                eq(1L),
                eq(100L),
                any(ProjectUpdateRequest.class)
        );
    }

    @Test
    void update_notFound_returns404()
            throws Exception {

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");

        when(projectService.update(
                eq(1L),
                eq(999L),
                any(ProjectUpdateRequest.class)
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        patch("/api/projects/999")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("프로젝트를 찾을 수 없습니다.")
                );
    }

    @Test
    void update_completedProject_returns409()
            throws Exception {

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");

        when(projectService.update(
                eq(1L),
                eq(100L),
                any(ProjectUpdateRequest.class)
        )).thenThrow(
                new CompletedProjectModificationException()
        );

        mockMvc.perform(
                        patch("/api/projects/100")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value("완성된 프로젝트는 수정할 수 없습니다.")
                );
    }

    @Test
    void update_invalidGauge_returns400()
            throws Exception {

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setPatternGaugeStitchCount(0);

        mockMvc.perform(
                        patch("/api/projects/100")
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
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectService);
    }

    @Test
    void update_withoutAuthentication_returns401()
            throws Exception {

        ProjectUpdateRequest request =
                new ProjectUpdateRequest();

        request.setSize("M");

        mockMvc.perform(
                        patch("/api/projects/100")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }

    @Test
    void start_success() throws Exception {

        // given
        ProjectDetailResponse response =
                new ProjectDetailResponse();

        response.setProjectId(100L);
        response.setTotalPages(32);
        response.setStatus(ProjectStatus.IN_PROGRESS);
        response.setStartDate(LocalDate.of(2026, 9, 28));

        when(projectService.start(
                1L,
                100L
        )).thenReturn(response);


        // when & then
        mockMvc.perform(
                        patch("/api/projects/100/start")
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
                        jsonPath("$.projectId")
                                .value(100L)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("IN_PROGRESS")
                )
                .andExpect(
                        jsonPath("$.startDate")
                                .value("2026-09-28")
                );

        verify(projectService)
                .start(1L, 100L);
    }

    @Test
    void start_inProgressProject_returns409()
            throws Exception {

        when(projectService.start(
                1L,
                100L
        )).thenThrow(
                new InvalidProjectStatusException(
                        "준비 중인 프로젝트만 시작할 수 있습니다."
                )
        );

        mockMvc.perform(
                        patch("/api/projects/100/start")
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
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "준비 중인 프로젝트만 시작할 수 있습니다."
                                )
                );
    }

    @Test
    void start_notFound_returns404()
            throws Exception {

        when(projectService.start(
                1L,
                999L
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        patch("/api/projects/999/start")
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
    void start_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        patch("/api/projects/100/start")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }

    @Test
    void updateCurrentPage_success() throws Exception {

        // given
        ProjectDetailResponse response =
                new ProjectDetailResponse();

        response.setProjectId(100L);
        response.setStatus(ProjectStatus.IN_PROGRESS);
        response.setCurrentPage(10);
        response.setTotalPages(20);

        when(projectService.updateCurrentPage(
                eq(1L),
                eq(100L),
                any(ProjectCurrentPageUpdateRequest.class)
        )).thenReturn(response);

        String body = """
            {
              "currentPage": 10
            }
            """;


        // when & then
        mockMvc.perform(
                        patch("/api/projects/100/current-page")
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
                .andExpect(jsonPath("$.projectId").value(100L))
                .andExpect(jsonPath("$.currentPage").value(10))
                .andExpect(jsonPath("$.totalPages").value(20));

        verify(projectService).updateCurrentPage(
                eq(1L),
                eq(100L),
                any(ProjectCurrentPageUpdateRequest.class)
        );
    }

    @Test
    void updateCurrentPage_zero_returns400()
            throws Exception {

        String body = """
            {
              "currentPage": 0
            }
            """;

        mockMvc.perform(
                        patch("/api/projects/100/current-page")
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

        verifyNoInteractions(projectService);
    }

    @Test
    void updateCurrentPage_exceedsTotalPages_returns400()
            throws Exception {

        when(projectService.updateCurrentPage(
                eq(1L),
                eq(100L),
                any(ProjectCurrentPageUpdateRequest.class)
        )).thenThrow(
                new InvalidCurrentPageException()
        );

        String body = """
            {
              "currentPage": 21
            }
            """;

        mockMvc.perform(
                        patch("/api/projects/100/current-page")
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
                                        "현재 페이지가 도안의 전체 페이지 범위를 벗어났습니다."
                                )
                );
    }

    @Test
    void updateCurrentPage_projectNotFound_returns404()
            throws Exception {

        when(projectService.updateCurrentPage(
                eq(1L),
                eq(999L),
                any(ProjectCurrentPageUpdateRequest.class)
        )).thenThrow(
                new ProjectNotFoundException()
        );

        String body = """
            {
              "currentPage": 10
            }
            """;

        mockMvc.perform(
                        patch("/api/projects/999/current-page")
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
    void updateCurrentPage_completedProject_returns409()
            throws Exception {

        when(projectService.updateCurrentPage(
                eq(1L),
                eq(100L),
                any(ProjectCurrentPageUpdateRequest.class)
        )).thenThrow(
                new CompletedProjectModificationException()
        );

        String body = """
            {
              "currentPage": 10
            }
            """;

        mockMvc.perform(
                        patch("/api/projects/100/current-page")
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
                                .value("완성된 프로젝트는 수정할 수 없습니다.")
                );
    }

    @Test
    void delete_success() throws Exception {

        mockMvc.perform(
                        delete("/api/projects/100")
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
                .andExpect(status().isNoContent());

        verify(projectService)
                .delete(
                        1L,
                        100L
                );
    }

    @Test
    void delete_notFound_returns404()
            throws Exception {

        doThrow(new ProjectNotFoundException())
                .when(projectService)
                .delete(
                        1L,
                        999L
                );

        mockMvc.perform(
                        delete("/api/projects/999")
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
    void delete_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        delete("/api/projects/100")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }

    @Test
    void complete_success() throws Exception {

        // given
        ProjectDetailResponse response =
                new ProjectDetailResponse();

        response.setProjectId(100L);
        response.setStatus(ProjectStatus.COMPLETED);
        response.setCompletedDate(
                LocalDate.of(2026, 9, 30)
        );

        when(projectService.complete(
                1L,
                100L
        )).thenReturn(response);


        // when & then
        mockMvc.perform(
                        patch("/api/projects/100/complete")
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
                        jsonPath("$.projectId")
                                .value(100L)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.completedDate")
                                .value("2026-09-30")
                );

        verify(projectService)
                .complete(
                        1L,
                        100L
                );
    }

    @Test
    void complete_projectNotFound_returns404()
            throws Exception {

        when(projectService.complete(
                1L,
                999L
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        patch("/api/projects/999/complete")
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
                                .value(
                                        "프로젝트를 찾을 수 없습니다."
                                )
                );
    }

    @Test
    void complete_preparingProject_returns409()
            throws Exception {

        when(projectService.complete(
                1L,
                100L
        )).thenThrow(
                new InvalidProjectStatusException(
                        "진행 중인 프로젝트만 완성할 수 있습니다."
                )
        );

        mockMvc.perform(
                        patch("/api/projects/100/complete")
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
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "진행 중인 프로젝트만 완성할 수 있습니다."
                                )
                );
    }

    @Test
    void complete_alreadyCompleted_returns409()
            throws Exception {

        when(projectService.complete(
                1L,
                100L
        )).thenThrow(
                new InvalidProjectStatusException(
                        "진행 중인 프로젝트만 완성할 수 있습니다."
                )
        );

        mockMvc.perform(
                        patch("/api/projects/100/complete")
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
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "진행 중인 프로젝트만 완성할 수 있습니다."
                                )
                );
    }

    @Test
    void complete_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        patch("/api/projects/100/complete")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(projectService);
    }
}
