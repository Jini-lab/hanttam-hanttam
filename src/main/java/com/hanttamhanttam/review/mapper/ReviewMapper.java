package com.hanttamhanttam.review.mapper;

import com.hanttamhanttam.review.domain.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {

    void insert(Review review);

}
