package com.hanttamhanttam.user.mapper;

import com.hanttamhanttam.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByEmail(@Param("email") String email);
    void insert(User user);
    User findById(Long userId);

}