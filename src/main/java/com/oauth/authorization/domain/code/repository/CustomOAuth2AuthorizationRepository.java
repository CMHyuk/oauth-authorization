package com.oauth.authorization.domain.code.repository;

import com.oauth.authorization.domain.code.model.CustomOAuth2Authorization;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface CustomOAuth2AuthorizationRepository extends CustomAwareRepository<CustomOAuth2Authorization, String> {
}
