package com.hanttamhanttam.pattern.service;

import com.hanttamhanttam.common.file.FileStorage;
import com.hanttamhanttam.common.file.PdfProcessor;
import com.hanttamhanttam.pattern.domain.Pattern;
import com.hanttamhanttam.pattern.dto.PatternCreateRequest;
import com.hanttamhanttam.pattern.dto.PatternResponse;
import com.hanttamhanttam.pattern.dto.PatternUpdateRequest;
import com.hanttamhanttam.pattern.exception.PatternNotFoundException;
import com.hanttamhanttam.pattern.exception.PatternPdfNotFoundException;
import com.hanttamhanttam.pattern.mapper.PatternMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatternServiceTest {

    @Mock
    private PatternMapper patternMapper;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private PdfProcessor pdfProcessor;

    @Mock
    private MultipartFile pdf;

    @InjectMocks
    private PatternService patternService;

    @Test
    void create_success() {

        // given
        Long userId = 1L;

        PatternCreateRequest request =
                new PatternCreateRequest();

        request.setPatternName("Cable Sweater");
        request.setAuthor("PetiteKnit");

        Path pdfPath =
                Path.of("uploads/patterns/1/test.pdf");

        when(fileStorage.savePatternPdf(userId, pdf))
                .thenReturn(pdfPath);

        when(pdfProcessor.getTotalPages(pdfPath))
                .thenReturn(12);

        when(pdfProcessor.createThumbnail(pdfPath))
                .thenReturn(
                        "uploads/patterns/1/test.png"
                );

        // MyBatis useGeneratedKeys 동작 흉내
        doAnswer(invocation -> {

            Pattern pattern =
                    invocation.getArgument(0);

            pattern.setPatternId(10L);

            return null;

        }).when(patternMapper)
                .insert(any(Pattern.class));


        // when
        PatternResponse response =
                patternService.create(
                        userId,
                        request,
                        pdf
                );


        // then
        assertEquals(
                10L,
                response.getPatternId()
        );

        assertEquals(
                "Cable Sweater",
                response.getPatternName()
        );

        assertEquals(
                "PetiteKnit",
                response.getAuthor()
        );

        assertEquals(
                "uploads/patterns/1/test.png",
                response.getThumbnailPath()
        );

        assertEquals(
                12,
                response.getTotalPages()
        );

        ArgumentCaptor<Pattern> captor =
                ArgumentCaptor.forClass(Pattern.class);

        verify(patternMapper)
                .insert(captor.capture());

        Pattern savedPattern =
                captor.getValue();

        assertEquals(
                1L,
                savedPattern.getUserId()
        );

        assertEquals(
                "Cable Sweater",
                savedPattern.getPatternName()
        );

        assertEquals(
                "PetiteKnit",
                savedPattern.getAuthor()
        );

        assertEquals(
                "uploads/patterns/1/test.pdf",
                savedPattern.getPdfPath()
        );

        assertEquals(
                "uploads/patterns/1/test.png",
                savedPattern.getThumbnailPath()
        );

        assertEquals(
                12,
                savedPattern.getTotalPages()
        );
    }

    @Test
    void findAll_success() {

        // given
        Long userId = 1L;

        Pattern pattern1 = new Pattern();
        pattern1.setPatternId(2L);
        pattern1.setUserId(userId);
        pattern1.setPatternName("Cable Sweater");
        pattern1.setAuthor("PetiteKnit");
        pattern1.setThumbnailPath("thumbnail2.png");
        pattern1.setTotalPages(12);

        Pattern pattern2 = new Pattern();
        pattern2.setPatternId(1L);
        pattern2.setUserId(userId);
        pattern2.setPatternName("Sunday Sweater");
        pattern2.setAuthor("PetiteKnit");
        pattern2.setThumbnailPath("thumbnail1.png");
        pattern2.setTotalPages(8);

        when(patternMapper.findAllByUserId(userId))
                .thenReturn(List.of(pattern1, pattern2));


        // when
        List<PatternResponse> result =
                patternService.findAll(userId);


        // then
        assertEquals(2, result.size());

        assertEquals(
                "Cable Sweater",
                result.get(0).getPatternName()
        );

        assertEquals(
                "Sunday Sweater",
                result.get(1).getPatternName()
        );

        verify(patternMapper)
                .findAllByUserId(userId);
    }

    @Test
    void findById_success() {

        // given
        Long userId = 1L;
        Long patternId = 10L;

        Pattern pattern = new Pattern();
        pattern.setPatternId(patternId);
        pattern.setUserId(userId);
        pattern.setPatternName("Cable Sweater");
        pattern.setAuthor("PetiteKnit");
        pattern.setThumbnailPath("thumbnail.png");
        pattern.setTotalPages(12);

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(pattern);


        // when
        PatternResponse response =
                patternService.findById(
                        userId,
                        patternId
                );


        // then
        assertEquals(
                10L,
                response.getPatternId()
        );

        assertEquals(
                "Cable Sweater",
                response.getPatternName()
        );

        verify(patternMapper)
                .findById(patternId, userId);
    }

    @Test
    void findById_notFound_throwsException() {

        // given
        Long userId = 1L;
        Long patternId = 999L;

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(null);


        // when & then
        PatternNotFoundException exception =
                assertThrows(
                        PatternNotFoundException.class,
                        () -> patternService.findById(
                                userId,
                                patternId
                        )
                );

        assertEquals(
                "도안을 찾을 수 없습니다.",
                exception.getMessage()
        );

        assertEquals(
                "도안을 찾을 수 없습니다.",
                exception.getMessage()
        );
    }

    @Test
    void getPdfPath_success() throws Exception {

        // given
        Long userId = 1L;
        Long patternId = 10L;

        Path tempPdf =
                Files.createTempFile(
                        "pattern-test-",
                        ".pdf"
                );

        Pattern pattern = new Pattern();
        pattern.setPatternId(patternId);
        pattern.setUserId(userId);
        pattern.setPdfPath(tempPdf.toString());

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(pattern);


        // when
        Path result =
                patternService.getPdfPath(
                        userId,
                        patternId
                );


        // then
        assertEquals(tempPdf, result);

        Files.deleteIfExists(tempPdf);
    }

    @Test
    void getPdfPath_notFound_throwsException() {

        // given
        when(patternMapper.findById(
                999L,
                1L
        )).thenReturn(null);


        // when & then
        assertThrows(
                PatternNotFoundException.class,
                () -> patternService.getPdfPath(
                        1L,
                        999L
                )
        );
    }

    @Test
    void getPdfPath_fileNotFound_throwsException() {

        // given
        Pattern pattern = new Pattern();
        pattern.setPatternId(10L);
        pattern.setUserId(1L);
        pattern.setPdfPath(
                "not-exists/test.pdf"
        );

        when(patternMapper.findById(
                10L,
                1L
        )).thenReturn(pattern);


        // when & then
        PatternPdfNotFoundException exception =
                assertThrows(
                        PatternPdfNotFoundException.class,
                        () -> patternService.getPdfPath(
                                1L,
                                10L
                        )
                );

        assertEquals(
                "PDF 파일을 찾을 수 없습니다.",
                exception.getMessage()
        );
    }

    @Test
    void update_success() {

        // given
        Long userId = 1L;
        Long patternId = 10L;

        Pattern pattern = new Pattern();
        pattern.setPatternId(patternId);
        pattern.setUserId(userId);
        pattern.setPatternName("기존 도안명");
        pattern.setAuthor("기존 작가");
        pattern.setThumbnailPath("thumbnail.png");
        pattern.setTotalPages(12);

        PatternUpdateRequest request =
                new PatternUpdateRequest();

        request.setPatternName("수정된 도안명");
        request.setAuthor("수정된 작가");

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(pattern);


        // when
        PatternResponse response =
                patternService.update(
                        userId,
                        patternId,
                        request
                );


        // then
        assertEquals(
                "수정된 도안명",
                response.getPatternName()
        );

        assertEquals(
                "수정된 작가",
                response.getAuthor()
        );

        verify(patternMapper)
                .findById(patternId, userId);

        verify(patternMapper)
                .update(pattern);
    }

    @Test
    void update_notFound_throwsException() {

        // given
        Long userId = 1L;
        Long patternId = 999L;

        PatternUpdateRequest request =
                new PatternUpdateRequest();

        request.setPatternName("수정된 도안명");
        request.setAuthor("수정된 작가");

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(null);


        // when & then
        assertThrows(
                PatternNotFoundException.class,
                () -> patternService.update(
                        userId,
                        patternId,
                        request
                )
        );

        verify(patternMapper, never())
                .update(any(Pattern.class));
    }

    @Test
    void updateThumbnail_success() {

        // given
        Long userId = 1L;
        Long patternId = 10L;

        Pattern pattern = new Pattern();
        pattern.setPatternId(patternId);
        pattern.setUserId(userId);
        pattern.setThumbnailPath("old.png");

        MultipartFile thumbnail =
                mock(MultipartFile.class);

        when(patternMapper.findById(patternId, userId))
                .thenReturn(pattern);

        when(thumbnail.isEmpty())
                .thenReturn(false);

        when(thumbnail.getContentType())
                .thenReturn(MediaType.IMAGE_PNG_VALUE);

        when(fileStorage.savePatternThumbnail(
                userId,
                thumbnail
        )).thenReturn(
                "uploads/patterns/1/new.png"
        );

        // when
        PatternResponse response =
                patternService.updateThumbnail(
                        userId,
                        patternId,
                        thumbnail
                );

        // then
        assertEquals(
                "uploads/patterns/1/new.png",
                response.getThumbnailPath()
        );

        verify(patternMapper).updateThumbnail(
                patternId,
                userId,
                "uploads/patterns/1/new.png"
        );
    }

    @Test
    void updateThumbnail_invalidFileType_throwsException() {

        // given
        Pattern pattern = new Pattern();
        pattern.setPatternId(10L);
        pattern.setUserId(1L);

        MultipartFile thumbnail =
                mock(MultipartFile.class);

        when(patternMapper.findById(10L, 1L))
                .thenReturn(pattern);

        when(thumbnail.isEmpty())
                .thenReturn(false);

        when(thumbnail.getContentType())
                .thenReturn(MediaType.APPLICATION_PDF_VALUE);

        // when & then
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> patternService.updateThumbnail(
                                1L,
                                10L,
                                thumbnail
                        )
                );

        assertEquals(
                "대표 이미지는 JPG 또는 PNG 파일만 가능합니다.",
                exception.getMessage()
        );

        verify(fileStorage, never())
                .savePatternThumbnail(anyLong(), any());

        verify(patternMapper, never())
                .updateThumbnail(
                        anyLong(),
                        anyLong(),
                        anyString()
                );
    }

    @Test
    void updateThumbnail_emptyFile_throwsException() {

        Pattern pattern = new Pattern();

        MultipartFile thumbnail =
                mock(MultipartFile.class);

        when(patternMapper.findById(10L, 1L))
                .thenReturn(pattern);

        when(thumbnail.isEmpty())
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> patternService.updateThumbnail(
                        1L,
                        10L,
                        thumbnail
                )
        );

        verifyNoInteractions(fileStorage);
    }

    @Test
    void resetThumbnail_success() throws Exception {

        // given
        Long userId = 1L;
        Long patternId = 10L;

        Path tempPdf =
                Files.createTempFile(
                        "pattern-test-",
                        ".pdf"
                );

        Pattern pattern = new Pattern();
        pattern.setPatternId(patternId);
        pattern.setUserId(userId);
        pattern.setPdfPath(tempPdf.toString());
        pattern.setThumbnailPath("custom-thumbnail.png");

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(pattern);

        when(pdfProcessor.createThumbnail(tempPdf))
                .thenReturn("generated-thumbnail.png");


        // when
        PatternResponse response =
                patternService.resetThumbnail(
                        userId,
                        patternId
                );


        // then
        assertEquals(
                "generated-thumbnail.png",
                response.getThumbnailPath()
        );

        verify(pdfProcessor)
                .createThumbnail(tempPdf);

        verify(patternMapper)
                .updateThumbnail(
                        patternId,
                        userId,
                        "generated-thumbnail.png"
                );

        Files.deleteIfExists(tempPdf);
    }

    @Test
    void resetThumbnail_pdfNotFound_throwsException() {

        // given
        Pattern pattern = new Pattern();
        pattern.setPatternId(10L);
        pattern.setUserId(1L);
        pattern.setPdfPath(
                "not-exists/pattern.pdf"
        );

        when(patternMapper.findById(
                10L,
                1L
        )).thenReturn(pattern);


        // when & then
        assertThrows(
                PatternPdfNotFoundException.class,
                () -> patternService.resetThumbnail(
                        1L,
                        10L
                )
        );

        verify(pdfProcessor, never())
                .createThumbnail(any());

        verify(patternMapper, never())
                .updateThumbnail(
                        anyLong(),
                        anyLong(),
                        anyString()
                );
    }

    @Test
    void delete_success() {

        // given
        Long userId = 1L;
        Long patternId = 10L;

        Pattern pattern = new Pattern();
        pattern.setPatternId(patternId);
        pattern.setUserId(userId);

        when(patternMapper.findById(
                patternId,
                userId
        )).thenReturn(pattern);

        when(patternMapper.softDelete(
                patternId,
                userId
        )).thenReturn(1);


        // when
        patternService.delete(
                userId,
                patternId
        );


        // then
        verify(patternMapper)
                .findById(patternId, userId);

        verify(patternMapper)
                .softDelete(patternId, userId);
    }

    @Test
    void delete_notFound_throwsException() {

        // given
        when(patternMapper.findById(
                999L,
                1L
        )).thenReturn(null);


        // when & then
        assertThrows(
                PatternNotFoundException.class,
                () -> patternService.delete(
                        1L,
                        999L
                )
        );

        verify(patternMapper, never())
                .softDelete(
                        anyLong(),
                        anyLong()
                );
    }
}
