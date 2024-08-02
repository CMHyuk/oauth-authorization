package com.oauth.authorization.domain.user.service;

import com.oauth.authorization.domain.user.dto.LoginRequest;
import com.oauth.authorization.domain.user.exception.UserErrorCode;
import com.oauth.authorization.domain.user.model.UserInfo;
import com.oauth.authorization.domain.user.repository.UserInfoQueryRepository;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserInfoQueryRepository userInfoQueryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void login(LoginRequest request) {
        UserInfo userInfo = userInfoQueryRepository.findByUserId(request.userId())
                .orElseThrow(() -> BusinessException.from(UserErrorCode.NOT_FOUND));
        validatePassword(request, userInfo);
    }

    private void validatePassword(LoginRequest request, UserInfo userInfo) {
        boolean matches = passwordEncoder.matches(userInfo.getPassword(), request.password());
        if (matches) {
            throw BusinessException.from(UserErrorCode.PASSWORD_MISMATCH);
        }
    }
}
