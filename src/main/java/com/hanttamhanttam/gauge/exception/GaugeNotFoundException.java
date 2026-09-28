package com.hanttamhanttam.gauge.exception;

public class GaugeNotFoundException extends RuntimeException {
    public GaugeNotFoundException() {
        super("게이지를 찾을 수 없습니다.");
    }
}
