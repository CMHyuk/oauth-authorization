package com.oauth.authorization.domain.code.repository;

import com.oauth.authorization.domain.code.model.OAuth2Authorization;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface OAuthAuthorizationRepository extends CustomAwareRepository<OAuth2Authorization, String> {
}
