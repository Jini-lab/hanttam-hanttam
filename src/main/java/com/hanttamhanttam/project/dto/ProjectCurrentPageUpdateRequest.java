package com.hanttamhanttam.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCurrentPageUpdateRequest {

    @NotNull(message = "현재 페이지를 입력해주세요.")
    @Positive(message = "현재 페이지는 1 이상이어야 합니다.")
    private Integer currentPage;

}
