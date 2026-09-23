package com.hanttamhanttam.pattern.exception;

public class PatternNotFoundException extends RuntimeException{

    public PatternNotFoundException() {
        super("도안을 찾을 수 없습니다.");
    }
}
