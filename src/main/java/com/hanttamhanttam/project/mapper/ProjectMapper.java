package com.hanttamhanttam.project.mapper;

import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.dto.ProjectDetailResponse;
import com.hanttamhanttam.project.dto.ProjectListResponse;
import com.hanttamhanttam.project.dto.ProjectUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    void insert(Project project);

    List<ProjectListResponse> findAllByUserId(
            Long userId
    );

    ProjectDetailResponse findById(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );

    int update(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId,
            @Param("request") ProjectUpdateRequest request
    );

    int start(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );

    int touchUpdatedAt(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );
}
