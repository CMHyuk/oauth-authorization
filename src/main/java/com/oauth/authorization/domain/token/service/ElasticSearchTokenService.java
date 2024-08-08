package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import com.oauth.authorization.domain.token.repository.ElasticSearchTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchTokenService {

    private final ElasticSearchTokenRepository elasticSearchTokenRepository;

    public void save(OAuth2Authorization authorization, String tenantId) {
        String accessToken = authorization.getAccessToken().getToken().getTokenValue();
        String refreshToken = authorization.getRefreshToken().getToken().getTokenValue();
        ElasticSearchToken elasticSearchToken = new ElasticSearchToken(accessToken, refreshToken);
        elasticSearchTokenRepository.save(tenantId, elasticSearchToken);
    }
}
