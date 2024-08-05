package com.oauth.authorization.domain.code.repository;

import com.oauth.authorization.domain.code.model.OAuthAuthorizationCode;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface OAuthAuthorizationCodeRepository extends CustomAwareRepository<OAuthAuthorizationCode, String> {
}
