package com.hanttamhanttam.worklog.exception;

public class InvalidWorkLogContentException extends RuntimeException {
    public InvalidWorkLogContentException() {
        super("작업 내용은 비어 있을 수 없습니다.");
    }
}
