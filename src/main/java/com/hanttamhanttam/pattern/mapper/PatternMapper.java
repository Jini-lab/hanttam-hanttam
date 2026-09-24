package com.hanttamhanttam.pattern.mapper;

import com.hanttamhanttam.pattern.domain.Pattern;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PatternMapper {

    void insert(Pattern pattern);

    Pattern findById(
            @Param("patternId") Long patternId,
            @Param("userId") Long userId
    );

    List<Pattern> findAllByUserId(
            @Param("userId") Long userId
    );

    void update(Pattern pattern);

    void updateThumbnail(
            @Param("patternId") Long patternId,
            @Param("userId") Long userId,
            @Param("thumbnailPath") String thumbnailPath
    );

    int softDelete(
            @Param("patternId") Long patternId,
            @Param("userId") Long userId
    );
}
