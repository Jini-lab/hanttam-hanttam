package com.hanttamhanttam.pattern.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatternUpdateRequest {

    @NotBlank(message = "도안명을 입력해주세요.")
    private String patternName;

    @NotBlank(message = "작가명을 입력해주세요.")
    private String author;
}
