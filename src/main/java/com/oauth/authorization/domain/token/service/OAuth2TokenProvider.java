package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.dto.OAuth2TokenResponse;
import com.oauth.authorization.global.util.ApiResponseExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2TokenProvider implements TokenProvider {

    private final OAuth2TokenProviderApi oAuth2TokenProviderApi;

    @Override
    public OAuth2TokenResponse getAccessToken(String authorizationCode, String redirectUri) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", "authorization_code");
        formParams.put("code", authorizationCode);
        formParams.put("redirect_uri", redirectUri);
        ResponseEntity<OAuth2TokenResponse> response = oAuth2TokenProviderApi.getToken(formParams);
        return ApiResponseExtractor.getBody(response);
    }
}
