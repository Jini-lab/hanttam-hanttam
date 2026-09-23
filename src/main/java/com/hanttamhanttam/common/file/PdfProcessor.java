package com.hanttamhanttam.common.file;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class PdfProcessor {

    public int getTotalPages(Path pdfPath) {

        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new IllegalStateException("PDF 정보를 읽을 수 없습니다.", e);
        }
    }

    public String createThumbnail(Path pdfPath) {
        try (PDDocument document =
                     Loader.loadPDF(pdfPath.toFile())) {

            PDFRenderer renderer =
                    new PDFRenderer(document);

            BufferedImage image =
                    renderer.renderImageWithDPI(
                            0,
                            120
                    );

            String pdfFilename =
                    pdfPath.getFileName().toString();

            String thumbnailFilename =
                    pdfFilename.replace(
                            ".pdf",
                            ".png"
                    );

            Path thumbnailPath =
                    pdfPath.getParent()
                            .resolve(thumbnailFilename);

            ImageIO.write(
                    image,
                    "png",
                    thumbnailPath.toFile()
            );

            return thumbnailPath.toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "PDF 썸네일 생성에 실패했습니다.",
                    e
            );
        }
    }
}
