package com.hanttamhanttam.worklog.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkLogUpdateRequest {

    @Positive(message = "도안 페이지는 1 이상이어야 합니다.")
    private Integer patternPage;

    private String content;
}
