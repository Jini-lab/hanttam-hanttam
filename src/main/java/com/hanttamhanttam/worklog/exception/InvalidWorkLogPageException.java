package com.hanttamhanttam.worklog.exception;

public class InvalidWorkLogPageException extends RuntimeException {
    public InvalidWorkLogPageException() {

        super("작업 기록 페이지가 도안의 전체 페이지 범위를 벗어났습니다.");
    }
}
