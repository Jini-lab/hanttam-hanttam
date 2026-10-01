package com.hanttamhanttam.worklog.dto;

import com.hanttamhanttam.worklog.domain.WorkLog;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WorkLogResponse {

    private final Long workLogId;
    private final Long projectId;

    private final Integer patternPage;
    private final String content;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public WorkLogResponse(WorkLog workLog) {
        this.workLogId = workLog.getWorkLogId();
        this.projectId = workLog.getProjectId();
        this.patternPage = workLog.getPatternPage();
        this.content = workLog.getContent();
        this.createdAt = workLog.getCreatedAt();
        this.updatedAt = workLog.getUpdatedAt();
    }
}
