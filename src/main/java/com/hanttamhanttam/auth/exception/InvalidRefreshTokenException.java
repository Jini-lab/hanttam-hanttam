package com.hanttamhanttam.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("유효하지 않은 Refresh Token입니다.");
    }
}
