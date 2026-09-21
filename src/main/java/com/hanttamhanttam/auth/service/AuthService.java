package com.hanttamhanttam.auth.service;

import com.hanttamhanttam.auth.domain.RefreshToken;
import com.hanttamhanttam.auth.dto.*;
import com.hanttamhanttam.auth.exception.InvalidCredentialsException;
import com.hanttamhanttam.auth.exception.InvalidRefreshTokenException;
import com.hanttamhanttam.auth.mapper.RefreshTokenMapper;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.common.security.TokenHasher;
import com.hanttamhanttam.user.domain.User;
import com.hanttamhanttam.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenHasher tokenHasher;
    private final RefreshTokenMapper refreshTokenMapper;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        User existingUser = userMapper.findByEmail(request.getEmail());

        if (existingUser != null) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());

        userMapper.insert(user);

        return new SignupResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    public User authenticate(LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail());

        if(user == null) {
            throw new InvalidCredentialsException();
        }
        if(!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException();
        }
        return user;
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        // 1. 이메일 / 비밀번호 검증
        User user = authenticate(request);

        // 2. Access Token 생성
        String accessToken =
                jwtProvider.createAccessToken(user.getUserId());

        // 3. Refresh Token 생성
        String refreshToken =
                jwtProvider.createRefreshToken(user.getUserId());

        // 4. Refresh Token 해시
        String tokenHash =
                tokenHasher.hash(refreshToken);

        // 5. DB에 저장할 객체 생성
        RefreshToken refreshTokenEntity =
                new RefreshToken();

        refreshTokenEntity.setUserId(user.getUserId());
        refreshTokenEntity.setTokenHash(tokenHash);
        refreshTokenEntity.setExpiresAt(
                jwtProvider.getRefreshTokenExpiresAt()
        );

        // 6. 저장 또는 교체
        refreshTokenMapper.upsert(refreshTokenEntity);

        // 7. Controller에 전달
        return new LoginResult(
                accessToken,
                refreshToken
        );
    }

    public String refresh(String refreshToken) {
        // 1. JWT 자체가 유효한지 확인
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        // 2. JWT에서 사용자 식별
        Long userId =
                jwtProvider.getUserId(refreshToken);

        // 3. DB에 저장된 Refresh Token 조회
        RefreshToken savedToken =
                refreshTokenMapper.findByUserId(userId);

        if (savedToken == null) {
            throw new InvalidRefreshTokenException();
        }

        // 4. 전달받은 Refresh Token을 동일하게 SHA-256
        String tokenHash =
                tokenHasher.hash(refreshToken);

        // 5. DB hash와 비교
        if (!tokenHash.equals(savedToken.getTokenHash())) {
            throw new InvalidRefreshTokenException();
        }

        // 6. 새로운 Access Token 발급
        return jwtProvider.createAccessToken(userId);
    }
}
