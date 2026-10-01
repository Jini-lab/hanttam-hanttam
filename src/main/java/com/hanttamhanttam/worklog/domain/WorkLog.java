package com.hanttamhanttam.worklog.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkLog {

    private Long workLogId;
    private Long projectId;

    private Integer patternPage;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
