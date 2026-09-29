package com.hanttamhanttam.project.exception;

public class InvalidCurrentPageException extends RuntimeException {
    public InvalidCurrentPageException() {
        super("현재 페이지가 도안의 전체 페이지 범위를 벗어났습니다.");
    }
}
