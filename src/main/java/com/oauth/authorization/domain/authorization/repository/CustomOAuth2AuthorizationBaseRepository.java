package com.oauth.authorization.domain.authorization.repository;

import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface CustomOAuth2AuthorizationBaseRepository extends CustomAwareRepository<CustomOAuth2Authorization, String> {
}
