package com.hanttamhanttam.project.mapper;

import com.hanttamhanttam.project.domain.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper {

    void insert(Project project);
}
