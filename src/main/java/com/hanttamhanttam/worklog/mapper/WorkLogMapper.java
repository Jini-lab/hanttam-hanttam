package com.hanttamhanttam.worklog.mapper;

import com.hanttamhanttam.worklog.domain.WorkLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkLogMapper {

    void insert(WorkLog workLog);

    List<WorkLog> findAllByProjectId(
            Long projectId
    );
}
