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
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@AllArgsConstructor
public class PatternService {

    private final PatternMapper patternMapper;
    private final FileStorage fileStorage;
    private final PdfProcessor pdfProcessor;

    public PatternResponse create(
            Long userId,
            PatternCreateRequest request,
            MultipartFile pdf
    ) {
        Path pdfPath = fileStorage.savePatternPdf(userId, pdf);
        int totalPages = pdfProcessor.getTotalPages(pdfPath);
        String thumbnailPath = pdfProcessor.createThumbnail(pdfPath);

        Pattern pattern = new Pattern();
        pattern.setUserId(userId);
        pattern.setPatternName(request.getPatternName());
        pattern.setAuthor(request.getAuthor());
        pattern.setPdfPath(pdfPath.toString());
        pattern.setThumbnailPath(thumbnailPath);
        pattern.setTotalPages(totalPages);

        patternMapper.insert(pattern);

        return new PatternResponse(pattern);
    }

    public List<PatternResponse> findAll(Long userId) {

        return patternMapper.findAllByUserId(userId)
                .stream()
                .map(PatternResponse::new)
                .toList();
    }

    public PatternResponse findById(
            Long userId,
            Long patternId
    ) {

        Pattern pattern =
                patternMapper.findById(
                        patternId,
                        userId
                );

        if (pattern == null) {
            throw new PatternNotFoundException();
        }

        return new PatternResponse(pattern);
    }

    public Path getPdfPath(
            Long userId,
            Long patternId
    ) {

        Pattern pattern =
                patternMapper.findById(
                        patternId,
                        userId
                );

        if (pattern == null) {
            throw new PatternNotFoundException();
        }

        Path pdfPath =
                Path.of(pattern.getPdfPath());

        if (!Files.exists(pdfPath)) {
            throw new PatternPdfNotFoundException();
        }

        return pdfPath;
    }

    public PatternResponse update(
            Long userId,
            Long patternId,
            PatternUpdateRequest request
    ) {

        Pattern pattern =
                patternMapper.findById(
                        patternId,
                        userId
                );

        if (pattern == null) {
            throw new PatternNotFoundException();
        }

        pattern.setPatternName(
                request.getPatternName()
        );

        pattern.setAuthor(
                request.getAuthor()
        );

        patternMapper.update(pattern);

        return new PatternResponse(pattern);
    }

    public PatternResponse updateThumbnail(
            Long userId,
            Long patternId,
            MultipartFile thumbnail
    ) {

        Pattern pattern =
                patternMapper.findById(patternId, userId);

        if (pattern == null) {
            throw new PatternNotFoundException();
        }

        if (thumbnail.isEmpty()) {
            throw new IllegalArgumentException(
                    "대표 이미지 파일이 비어있습니다."
            );
        }

        String contentType =
                thumbnail.getContentType();

        if (!MediaType.IMAGE_JPEG_VALUE.equals(contentType)
                && !MediaType.IMAGE_PNG_VALUE.equals(contentType)) {

            throw new IllegalArgumentException(
                    "대표 이미지는 JPG 또는 PNG 파일만 가능합니다."
            );
        }

        String thumbnailPath =
                fileStorage.savePatternThumbnail(
                        userId,
                        thumbnail
                );

        patternMapper.updateThumbnail(
                patternId,
                userId,
                thumbnailPath
        );

        pattern.setThumbnailPath(thumbnailPath);

        return new PatternResponse(pattern);
    }

    public PatternResponse resetThumbnail(
            Long userId,
            Long patternId
    ) {

        Pattern pattern =
                patternMapper.findById(
                        patternId,
                        userId
                );

        if (pattern == null) {
            throw new PatternNotFoundException();
        }

        Path pdfPath =
                Path.of(pattern.getPdfPath());

        if (!Files.exists(pdfPath)) {
            throw new PatternPdfNotFoundException();
        }

        String thumbnailPath =
                pdfProcessor.createThumbnail(pdfPath);

        patternMapper.updateThumbnail(
                patternId,
                userId,
                thumbnailPath
        );

        pattern.setThumbnailPath(thumbnailPath);

        return new PatternResponse(pattern);
    }
}
