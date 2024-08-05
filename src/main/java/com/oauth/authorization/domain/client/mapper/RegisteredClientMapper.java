package com.oauth.authorization.domain.client.mapper;

import com.oauth.authorization.domain.client.model.ClientInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisteredClientMapper {

    public RegisteredClient toRegisteredClient(ClientInfo clientInfo) {
        return RegisteredClient.withId(clientInfo.getId())
                .clientId(clientInfo.getClientId())
                .clientSecret(clientInfo.getClientSecret())
                .clientName(clientInfo.getClientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scopes(scopes -> scopes.addAll(clientInfo.getScopes()))
                .redirectUris(redirectUris -> redirectUris.addAll(clientInfo.getRegisteredRedirectUris()))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
    }
}
