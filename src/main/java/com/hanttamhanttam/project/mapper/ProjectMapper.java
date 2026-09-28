package com.hanttamhanttam.project.mapper;

import com.hanttamhanttam.project.domain.Project;
import com.hanttamhanttam.project.dto.ProjectListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {

    void insert(Project project);

    List<ProjectListResponse> findAllByUserId(
            Long userId
    );
}
