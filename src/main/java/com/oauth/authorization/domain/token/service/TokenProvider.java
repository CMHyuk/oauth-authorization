package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.dto.OAuth2TokenResponse;

public interface TokenProvider {

    OAuth2TokenResponse getAccessToken(String authorizationCode, String redirectUri);
}
