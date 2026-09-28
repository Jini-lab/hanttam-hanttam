package com.hanttamhanttam.project.exception;

public class CompletedProjectModificationException extends RuntimeException{

    public CompletedProjectModificationException() {
        super("완성된 프로젝트는 수정할 수 없습니다.");
    }
}
