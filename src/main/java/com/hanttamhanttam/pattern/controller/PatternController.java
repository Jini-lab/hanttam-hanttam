package com.hanttamhanttam.pattern.controller;

import com.hanttamhanttam.pattern.dto.PatternCreateRequest;
import com.hanttamhanttam.pattern.dto.PatternResponse;
import com.hanttamhanttam.pattern.dto.PatternUpdateRequest;
import com.hanttamhanttam.pattern.service.PatternService;
import jakarta.validation.Valid;

import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
public class PatternController {

    private final PatternService patternService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PatternResponse> create(
            Authentication authentication,

            @Valid
            @RequestPart("request")
            PatternCreateRequest request,

            @RequestPart("pdf")
            MultipartFile pdf
    ) {
        Long userId = (Long) authentication.getPrincipal();

        PatternResponse response = patternService.create(
                userId,
                request,
                pdf
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PatternResponse>> findAll(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<PatternResponse> patterns =
                patternService.findAll(userId);

        return ResponseEntity.ok(patterns);
    }

    @GetMapping("/{patternId}")
    public ResponseEntity<PatternResponse> findById(
            Authentication authentication,
            @PathVariable Long patternId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        PatternResponse response =
                patternService.findById(
                        userId,
                        patternId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/{patternId}/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<Resource> getPdf(
            Authentication authentication,
            @PathVariable Long patternId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        Path pdfPath =
                patternService.getPdfPath(
                        userId,
                        patternId
                );

        Resource resource =
                new FileSystemResource(pdfPath);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @PatchMapping("/{patternId}")
    public ResponseEntity<PatternResponse> update(
            Authentication authentication,
            @PathVariable Long patternId,
            @Valid @RequestBody
            PatternUpdateRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        PatternResponse response =
                patternService.update(
                        userId,
                        patternId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping(
            value = "/{patternId}/thumbnail",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<PatternResponse> updateThumbnail(
            Authentication authentication,
            @PathVariable Long patternId,
            @RequestPart("thumbnail")
            MultipartFile thumbnail
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        PatternResponse response =
                patternService.updateThumbnail(
                        userId,
                        patternId,
                        thumbnail
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patternId}/thumbnail")
    public ResponseEntity<PatternResponse> resetThumbnail(
            Authentication authentication,
            @PathVariable Long patternId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        PatternResponse response =
                patternService.resetThumbnail(
                        userId,
                        patternId
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patternId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long patternId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        patternService.delete(
                userId,
                patternId
        );

        return ResponseEntity.noContent().build();
    }
}
