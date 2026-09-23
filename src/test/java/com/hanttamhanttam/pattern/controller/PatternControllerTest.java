package com.hanttamhanttam.pattern.controller;

import com.hanttamhanttam.common.config.SecurityConfig;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import com.hanttamhanttam.common.security.JwtAuthenticationFilter;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.pattern.domain.Pattern;
import com.hanttamhanttam.pattern.dto.PatternCreateRequest;
import com.hanttamhanttam.pattern.dto.PatternResponse;
import com.hanttamhanttam.pattern.dto.PatternUpdateRequest;
import com.hanttamhanttam.pattern.exception.PatternNotFoundException;
import com.hanttamhanttam.pattern.service.PatternService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatternController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
class PatternControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatternService patternService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void create_success() throws Exception {

        // given
        PatternCreateRequest request =
                new PatternCreateRequest();

        request.setPatternName("Cable Sweater");
        request.setAuthor("PetiteKnit");

        MockMultipartFile requestPart =
                new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

        MockMultipartFile pdfPart =
                new MockMultipartFile(
                        "pdf",
                        "cable-sweater.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "fake-pdf-content".getBytes()
                );

        PatternResponse response =
                new PatternResponse(
                        createPattern()
                );

        when(patternService.create(
                eq(1L),
                any(PatternCreateRequest.class),
                any()
        )).thenReturn(response);


        // when & then
        mockMvc.perform(
                        multipart("/api/patterns")
                                .file(requestPart)
                                .file(pdfPart)
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
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.patternId")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.patternName")
                                .value("Cable Sweater")
                )
                .andExpect(
                        jsonPath("$.author")
                                .value("PetiteKnit")
                )
                .andExpect(
                        jsonPath("$.thumbnailPath")
                                .value("uploads/patterns/1/test.png")
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(12)
                );

        verify(patternService).create(
                eq(1L),
                any(PatternCreateRequest.class),
                any()
        );
    }

    private Pattern createPattern() {

        Pattern pattern = new Pattern();

        pattern.setPatternId(10L);
        pattern.setUserId(1L);
        pattern.setPatternName("Cable Sweater");
        pattern.setAuthor("PetiteKnit");
        pattern.setPdfPath(
                "uploads/patterns/1/test.pdf"
        );
        pattern.setThumbnailPath(
                "uploads/patterns/1/test.png"
        );
        pattern.setTotalPages(12);

        return pattern;
    }

    @Test
    void create_withoutAuthentication_returns401()
            throws Exception {

        PatternCreateRequest request =
                new PatternCreateRequest();

        request.setPatternName("Cable Sweater");
        request.setAuthor("PetiteKnit");

        MockMultipartFile requestPart =
                new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(request)
                );

        MockMultipartFile pdfPart =
                new MockMultipartFile(
                        "pdf",
                        "cable-sweater.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "fake-pdf-content".getBytes()
                );

        mockMvc.perform(
                        multipart("/api/patterns")
                                .file(requestPart)
                                .file(pdfPart)
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(patternService);
    }

    @Test
    void findAll_success() throws Exception {

        Pattern pattern = createPattern();

        when(patternService.findAll(1L))
                .thenReturn(
                        List.of(
                                new PatternResponse(pattern)
                        )
                );

        mockMvc.perform(
                        get("/api/patterns")
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
                .andExpect(jsonPath("$[0].patternId").value(10L))
                .andExpect(
                        jsonPath("$[0].patternName")
                                .value("Cable Sweater")
                )
                .andExpect(
                        jsonPath("$[0].author")
                                .value("PetiteKnit")
                );

        verify(patternService)
                .findAll(1L);
    }

    @Test
    void findAll_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/patterns")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(patternService);
    }

    @Test
    void findById_success() throws Exception {

        // given
        Pattern pattern = createPattern();

        when(patternService.findById(
                1L,
                10L
        )).thenReturn(
                new PatternResponse(pattern)
        );

        // when & then
        mockMvc.perform(
                        get("/api/patterns/10")
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
                        jsonPath("$.patternId")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.patternName")
                                .value("Cable Sweater")
                )
                .andExpect(
                        jsonPath("$.author")
                                .value("PetiteKnit")
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(12)
                );

        verify(patternService)
                .findById(1L, 10L);
    }

    @Test
    void findById_notFound_returns404()
            throws Exception {

        // given
        when(patternService.findById(
                1L,
                999L
        )).thenThrow(
                new PatternNotFoundException()
        );

        // when & then
        mockMvc.perform(
                        get("/api/patterns/999")
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
                                .value("도안을 찾을 수 없습니다.")
                );
    }

    @Test
    void findById_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/patterns/10")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(patternService);
    }

    @Test
    void getPdf_success() throws Exception {

        // given
        Path tempPdf =
                Files.createTempFile(
                        "pattern-test-",
                        ".pdf"
                );

        Files.write(
                tempPdf,
                "fake-pdf-content".getBytes()
        );

        when(patternService.getPdfPath(
                1L,
                10L
        )).thenReturn(tempPdf);

        // when & then
        mockMvc.perform(
                        get("/api/patterns/10/pdf")
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
                        content().contentType(
                                MediaType.APPLICATION_PDF
                        )
                )
                .andExpect(
                        content().bytes(
                                "fake-pdf-content".getBytes()
                        )
                );

        verify(patternService)
                .getPdfPath(1L, 10L);

        Files.deleteIfExists(tempPdf);
    }

    @Test
    void getPdf_patternNotFound_returns404()
            throws Exception {

        // given
        when(patternService.getPdfPath(
                1L,
                999L
        )).thenThrow(
                new PatternNotFoundException()
        );

        // when & then
        mockMvc.perform(
                        get("/api/patterns/999/pdf")
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
                                .value("도안을 찾을 수 없습니다.")
                );
    }

    @Test
    void getPdf_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(
                        get("/api/patterns/10/pdf")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(patternService);
    }

    @Test
    void update_success() throws Exception {

        // given
        PatternUpdateRequest request =
                new PatternUpdateRequest();

        request.setPatternName("수정된 도안명");
        request.setAuthor("수정된 작가");

        Pattern pattern = createPattern();
        pattern.setPatternName("수정된 도안명");
        pattern.setAuthor("수정된 작가");

        when(patternService.update(
                eq(1L),
                eq(10L),
                any(PatternUpdateRequest.class)
        )).thenReturn(
                new PatternResponse(pattern)
        );


        // when & then
        mockMvc.perform(
                        patch("/api/patterns/10")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.patternId")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.patternName")
                                .value("수정된 도안명")
                )
                .andExpect(
                        jsonPath("$.author")
                                .value("수정된 작가")
                );
    }

    @Test
    void update_blankPatternName_returns400()
            throws Exception {

        PatternUpdateRequest request =
                new PatternUpdateRequest();

        request.setPatternName("");
        request.setAuthor("PetiteKnit");

        mockMvc.perform(
                        patch("/api/patterns/10")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patternService);
    }

    @Test
    void update_notFound_returns404()
            throws Exception {

        PatternUpdateRequest request =
                new PatternUpdateRequest();

        request.setPatternName("수정된 도안명");
        request.setAuthor("수정된 작가");

        when(patternService.update(
                eq(1L),
                eq(999L),
                any(PatternUpdateRequest.class)
        )).thenThrow(
                new PatternNotFoundException()
        );

        mockMvc.perform(
                        patch("/api/patterns/999")
                                .with(
                                        authentication(
                                                new UsernamePasswordAuthenticationToken(
                                                        1L,
                                                        null,
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("도안을 찾을 수 없습니다.")
                );
    }
}
