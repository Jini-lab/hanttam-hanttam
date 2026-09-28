package com.hanttamhanttam.gauge.controller;

import com.hanttamhanttam.common.config.SecurityConfig;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import com.hanttamhanttam.common.security.JwtAuthenticationFilter;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.gauge.domain.Gauge;
import com.hanttamhanttam.gauge.dto.GaugeCreateRequest;
import com.hanttamhanttam.gauge.dto.GaugeResponse;
import com.hanttamhanttam.gauge.service.GaugeService;
import com.hanttamhanttam.project.exception.CompletedProjectModificationException;
import com.hanttamhanttam.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GaugeController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
public class GaugeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GaugeService gaugeService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_success() throws Exception {

        // given
        GaugeResponse response =
                mock(GaugeResponse.class);

        when(response.getGaugeId()).thenReturn(1L);
        when(response.getProjectId()).thenReturn(100L);
        when(response.getYarnName()).thenReturn("Merino Wool");
        when(response.getNeedleSize())
                .thenReturn(new BigDecimal("4.50"));
        when(response.getStitchCount()).thenReturn(22);
        when(response.getRowCount()).thenReturn(30);
        when(response.getMeasuredWidthCm())
                .thenReturn(new BigDecimal("10.00"));
        when(response.getMeasuredHeightCm())
                .thenReturn(new BigDecimal("10.00"));
        when(response.getIsSelected()).thenReturn(false);

        when(gaugeService.create(
                eq(1L),
                eq(100L),
                any(GaugeCreateRequest.class)
        )).thenReturn(response);

        String body = """
            {
              "yarnName": "Merino Wool",
              "needleSize": 4.50,
              "stitchCount": 22,
              "rowCount": 30,
              "measuredWidthCm": 10.00,
              "measuredHeightCm": 10.00
            }
            """;

        // when & then
        mockMvc.perform(
                        post("/api/projects/100/gauges")
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
                .andExpect(jsonPath("$.gaugeId").value(1L))
                .andExpect(jsonPath("$.projectId").value(100L))
                .andExpect(jsonPath("$.yarnName").value("Merino Wool"))
                .andExpect(jsonPath("$.needleSize").value(4.50))
                .andExpect(jsonPath("$.stitchCount").value(22))
                .andExpect(jsonPath("$.rowCount").value(30))
                .andExpect(jsonPath("$.isSelected").value(false));
    }

    @Test
    void create_invalidRequest_returns400()
            throws Exception {

        String body = """
            {
              "yarnName": "",
              "needleSize": 0,
              "stitchCount": 0,
              "rowCount": 30,
              "measuredWidthCm": 10.00,
              "measuredHeightCm": 10.00
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/gauges")
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

        verifyNoInteractions(gaugeService);
    }

    @Test
    void create_projectNotFound_returns404()
            throws Exception {

        when(gaugeService.create(
                eq(1L),
                eq(999L),
                any(GaugeCreateRequest.class)
        )).thenThrow(
                new ProjectNotFoundException()
        );

        String body = """
            {
              "yarnName": "Merino Wool",
              "needleSize": 4.50,
              "stitchCount": 22,
              "rowCount": 30,
              "measuredWidthCm": 10.00,
              "measuredHeightCm": 10.00
            }
            """;

        mockMvc.perform(
                        post("/api/projects/999/gauges")
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
    void create_completedProject_returns409()
            throws Exception {

        when(gaugeService.create(
                eq(1L),
                eq(100L),
                any(GaugeCreateRequest.class)
        )).thenThrow(
                new CompletedProjectModificationException()
        );

        String body = """
            {
              "yarnName": "Merino Wool",
              "needleSize": 4.50,
              "stitchCount": 22,
              "rowCount": 30,
              "measuredWidthCm": 10.00,
              "measuredHeightCm": 10.00
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/gauges")
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
                                        "완성된 프로젝트는 수정할 수 없습니다."
                                )
                );
    }

    @Test
    void create_withoutAuthentication_returns401()
            throws Exception {

        String body = """
            {
              "yarnName": "Merino Wool",
              "needleSize": 4.50,
              "stitchCount": 22,
              "rowCount": 30,
              "measuredWidthCm": 10.00,
              "measuredHeightCm": 10.00
            }
            """;

        mockMvc.perform(
                        post("/api/projects/100/gauges")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(gaugeService);
    }

    @Test
    void findAll_success() throws Exception {

        // given
        Gauge gauge1 = new Gauge();
        gauge1.setGaugeId(1L);
        gauge1.setProjectId(100L);
        gauge1.setYarnName("Merino Wool");
        gauge1.setNeedleSize(new BigDecimal("4.50"));
        gauge1.setStitchCount(22);
        gauge1.setRowCount(30);
        gauge1.setMeasuredWidthCm(new BigDecimal("10.00"));
        gauge1.setMeasuredHeightCm(new BigDecimal("10.00"));
        gauge1.setIsSelected(false);

        Gauge gauge2 = new Gauge();
        gauge2.setGaugeId(2L);
        gauge2.setProjectId(100L);
        gauge2.setYarnName("Merino Wool");
        gauge2.setNeedleSize(new BigDecimal("5.00"));
        gauge2.setStitchCount(20);
        gauge2.setRowCount(28);
        gauge2.setMeasuredWidthCm(new BigDecimal("10.00"));
        gauge2.setMeasuredHeightCm(new BigDecimal("10.00"));
        gauge2.setIsSelected(false);

        when(gaugeService.findAll(
                1L,
                100L
        )).thenReturn(
                List.of(
                        new GaugeResponse(gauge1),
                        new GaugeResponse(gauge2)
                )
        );


        // when & then
        mockMvc.perform(
                        get("/api/projects/100/gauges")
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

                .andExpect(jsonPath("$[0].gaugeId").value(1L))
                .andExpect(jsonPath("$[0].needleSize").value(4.50))
                .andExpect(jsonPath("$[0].stitchCount").value(22))
                .andExpect(jsonPath("$[0].rowCount").value(30))
                .andExpect(jsonPath("$[0].isSelected").value(false))

                .andExpect(jsonPath("$[1].gaugeId").value(2L))
                .andExpect(jsonPath("$[1].needleSize").value(5.00))
                .andExpect(jsonPath("$[1].stitchCount").value(20))
                .andExpect(jsonPath("$[1].rowCount").value(28));

        verify(gaugeService)
                .findAll(1L, 100L);
    }

    @Test
    void findAll_projectNotFound_returns404()
            throws Exception {

        when(gaugeService.findAll(
                1L,
                999L
        )).thenThrow(
                new ProjectNotFoundException()
        );

        mockMvc.perform(
                        get("/api/projects/999/gauges")
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
                        get("/api/projects/100/gauges")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(gaugeService);
    }
}
