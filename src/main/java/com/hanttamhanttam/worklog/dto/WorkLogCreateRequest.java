package com.hanttamhanttam.worklog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkLogCreateRequest {

    @NotNull(message = "도안 페이지를 입력해주세요.")
    @Positive(message = "도안 페이지는 1 이상이어야 합니다.")
    private Integer patternPage;

    @NotBlank(message = "작업 내용을 입력해주세요.")
    private String content;
}
