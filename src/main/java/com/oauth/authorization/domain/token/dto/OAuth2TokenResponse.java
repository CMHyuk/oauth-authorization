package com.oauth.authorization.domain.token.dto;

public record OAuth2TokenResponse(
        String access_token,
        String refresh_token,
        String scope,
        String token_type,
        Integer expires_in
) {
}
