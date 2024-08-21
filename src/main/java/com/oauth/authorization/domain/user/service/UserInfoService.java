package com.oauth.authorization.domain.user.service;

import com.oauth.authorization.domain.user.exception.UserErrorCode;
import com.oauth.authorization.domain.user.model.UserInfo;
import com.oauth.authorization.domain.user.model.UserInfoAdapter;
import com.oauth.authorization.domain.user.repository.UserInfoRepository;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.from(UserErrorCode.NOT_FOUND));
        return new UserInfoAdapter(userInfo);
    }
}
