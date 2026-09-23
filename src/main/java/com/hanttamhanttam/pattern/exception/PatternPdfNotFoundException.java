package com.hanttamhanttam.pattern.exception;

public class PatternPdfNotFoundException extends RuntimeException {
    public PatternPdfNotFoundException() {
        super("PDF 파일을 찾을 수 없습니다.");
    }
}
