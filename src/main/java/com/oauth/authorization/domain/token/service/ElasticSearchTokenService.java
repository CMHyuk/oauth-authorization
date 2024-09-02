package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import com.oauth.authorization.domain.token.repository.ElasticSearchTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchTokenService {

    private final ElasticSearchTokenRepository elasticSearchTokenRepository;

    /**
     * code는 한 번밖에 사용하지 못하는데, 만약 이미 사용한 code로 토큰 발급을 요청하면 accessToken, refreshToken 모두 invalidated = true로 바꿈
     * 따라서 기존 발급된 refreshToken으로 acccessToken 재발급 불가능
     */
    public void save(CustomOAuth2Authorization customOAuth2Authorization, OAuth2Authorization authorization, String tenantId) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        elasticSearchTokenRepository.findByRefreshToken(refreshToken.getToken().getTokenValue())
                .ifPresentOrElse(
                        elasticSearchToken -> reissueAccessToken(authorization, tenantId, refreshToken),
                        () -> issueInitialAccessToken(customOAuth2Authorization, authorization, tenantId)
                );
    }

    private void reissueAccessToken(OAuth2Authorization authorization, String tenantId, OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken) {
        if (refreshToken.isActive()) {
            elasticSearchTokenRepository.save(tenantId, ElasticSearchToken.from(authorization));
        }
    }

    private void issueInitialAccessToken(CustomOAuth2Authorization customOAuth2Authorization, OAuth2Authorization authorization, String tenantId) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = customOAuth2Authorization.getOAuth2Authorization().getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode.isActive()) {
            elasticSearchTokenRepository.save(tenantId, ElasticSearchToken.from(authorization));
        }
    }
}
