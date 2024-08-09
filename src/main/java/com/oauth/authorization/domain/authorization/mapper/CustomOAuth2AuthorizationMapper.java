package com.oauth.authorization.domain.authorization.mapper;

import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2AuthorizationMapper {

    private static final String INITIAL_CODE = "EMPTY_CODE";

    public CustomOAuth2Authorization create(OAuth2Authorization.Token<OAuth2AuthorizationCode> token, String state, String authorizationId, OAuth2Authorization authentication) {
        return new CustomOAuth2Authorization(
                token == null ? INITIAL_CODE : token.getToken().getTokenValue(),
                state,
                authorizationId,
                authentication);
    }
}
