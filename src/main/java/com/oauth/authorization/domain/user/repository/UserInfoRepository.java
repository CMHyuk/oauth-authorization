package com.oauth.authorization.domain.user.repository;


import com.oauth.authorization.domain.user.model.UserInfo;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface UserInfoRepository extends CustomAwareRepository<UserInfo, String> {
}
