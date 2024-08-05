package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.dto.OAuth2TokenResponse;
import com.oauth.authorization.domain.token.model.ElasticSearchAccessToken;
import com.oauth.authorization.domain.token.repository.ElasticSearchAccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchAccessTokenService {

    private final ElasticSearchAccessTokenRepository elasticSearchAccessTokenRepository;

    public ElasticSearchAccessToken save(String ipAddress, OAuth2TokenResponse response) {
        ElasticSearchAccessToken elasticSearchAccessToken = ElasticSearchAccessToken.create(
                response.access_token(),
                response.refresh_token(),
                ipAddress
        );
        return elasticSearchAccessTokenRepository.save(null, elasticSearchAccessToken);
    }
}
