package com.hanttamhanttam.worklog.exception;

public class WorkLogNotFoundException extends RuntimeException {
    public WorkLogNotFoundException() {
        super("작업 기록을 찾을 수 없습니다.");
    }
}
