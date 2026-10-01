package com.hanttamhanttam.worklog.mapper;

import com.hanttamhanttam.worklog.domain.WorkLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkLogMapper {

    void insert(WorkLog workLog);

    List<WorkLog> findAllByProjectId(
            Long projectId
    );

    WorkLog findById(
            @Param("workLogId") Long workLogId,
            @Param("projectId") Long projectId
    );

    int update(WorkLog workLog);

    int delete(
            @Param("workLogId") Long workLogId,
            @Param("projectId") Long projectId
    );
}
