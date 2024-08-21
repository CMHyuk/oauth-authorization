package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import com.oauth.authorization.domain.token.repository.ElasticSearchTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchTokenService {

    private final ElasticSearchTokenRepository elasticSearchTokenRepository;

    public void save(CustomOAuth2Authorization customOAuth2Authorization, OAuth2Authorization authorization, String tenantId) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = customOAuth2Authorization.getOAuth2Authorization().getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode.isActive()) {
            elasticSearchTokenRepository.save(tenantId, ElasticSearchToken.from(authorization));
        }
        if (authorizationCode.isInvalidated()) {
            elasticSearchTokenRepository.save(tenantId, ElasticSearchToken.from(authorization));
        }
    }
}
