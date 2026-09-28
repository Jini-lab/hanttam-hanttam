package com.hanttamhanttam.project.controller;

import com.hanttamhanttam.common.config.SecurityConfig;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import com.hanttamhanttam.common.security.JwtAuthenticationFilter;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.domain.ProjectStatus;
import com.hanttamhanttam.project.dto.ProjectCreateRequest;
import com.hanttamhanttam.project.dto.ProjectListResponse;
import com.hanttamhanttam.project.dto.ProjectResponse;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
}
