package com.oauth.authorization.domain.token.dto;

import com.oauth.authorization.domain.token.model.ElasticSearchAccessToken;

public record AccessTokenSaveResponse(
        String accessToken,
        String authentication,
        String refreshToken,
        String ipAddress
) {

    public static AccessTokenSaveResponse from(ElasticSearchAccessToken response) {
        return new AccessTokenSaveResponse(response.getAccessToken(), "true", response.getRefreshToken(), response.getIpAddress());
    }
}
