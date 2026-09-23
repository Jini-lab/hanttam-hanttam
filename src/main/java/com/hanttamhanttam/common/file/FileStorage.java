package com.hanttamhanttam.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStorage {

    private final Path uploadDir;

    public FileStorage(
            @Value("${file.upload-dir}") String uploadDir
    ) {
        this.uploadDir = Paths.get(uploadDir);
    }

    public Path savePatternPdf(
            Long userId, MultipartFile file
    ) {
        try {
            Path directory = uploadDir
                    .resolve("patterns")
                    .resolve(String.valueOf(userId));

            Files.createDirectories(directory);

            String filename = UUID.randomUUID() + ".pdf";

            Path targetPath = directory.resolve(filename);

            file.transferTo(targetPath);

            return targetPath;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "PDF 파일 저장에 실패했습니다."
            );
        }
    }

    public String savePatternThumbnail(
            Long userId,
            MultipartFile file
    ) {
        try {
            Path directory = uploadDir
                    .resolve("patterns")
                    .resolve(String.valueOf(userId));

            Files.createDirectories(directory);

            String originalFilename =
                    file.getOriginalFilename();

            String extension = "";

            if (originalFilename != null
                    && originalFilename.contains(".")) {

                extension = originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );
            }

            String filename =
                    UUID.randomUUID() + extension;

            Path targetPath =
                    directory.resolve(filename);

            file.transferTo(targetPath);

            return targetPath.toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "대표 이미지 저장에 실패했습니다.",
                    e
            );
        }
    }

}
