package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.dto.OAuth2TokenResponse;
import com.oauth.authorization.domain.token.model.ElasticSearchAccessToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchAccessTokenFacade {

    private final ElasticSearchAccessTokenService elasticSearchAccessTokenService;
    private final TokenProvider tokenProvider;

    public ElasticSearchAccessToken save(String code, String redirectUri, String ipAddress) {
        OAuth2TokenResponse response = tokenProvider.getAccessToken(code, redirectUri);
        return elasticSearchAccessTokenService.save(ipAddress, response);
    }
}
