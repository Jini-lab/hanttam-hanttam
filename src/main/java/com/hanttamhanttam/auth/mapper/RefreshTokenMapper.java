package com.hanttamhanttam.auth.mapper;

import com.hanttamhanttam.auth.domain.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {

    void upsert(RefreshToken refreshToken);
}
