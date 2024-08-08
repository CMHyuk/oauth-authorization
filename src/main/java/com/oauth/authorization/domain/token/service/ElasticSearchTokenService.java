package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import com.oauth.authorization.domain.token.repository.ElasticSearchTokenQueryRepository;
import com.oauth.authorization.domain.token.repository.ElasticSearchTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ElasticSearchTokenService {

    private final ElasticSearchTokenQueryRepository elasticSearchTokenQueryRepository;
    private final ElasticSearchTokenRepository elasticSearchTokenRepository;

    public void save(OAuth2Authorization authorization, String tenantId) {
        String username = authorization.getPrincipalName();
        Optional<ElasticSearchToken> elasticSearchToken = elasticSearchTokenQueryRepository.findByUsername(username);
        if (elasticSearchToken.isEmpty()) {
            elasticSearchTokenRepository.save(tenantId, ElasticSearchToken.from(authorization));
        }
    }
}
