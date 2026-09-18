package com.hanttamhanttam.auth.service;

import com.hanttamhanttam.auth.dto.SignupRequest;
import com.hanttamhanttam.auth.dto.SignupResponse;
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
}
