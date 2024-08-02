package com.oauth.authorization.domain.client.service;

import com.oauth.authorization.domain.client.exception.ClientErrorCode;
import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.domain.client.repository.ClientInfoQueryRepository;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientInfoService implements RegisteredClientRepository {

    private final ClientInfoQueryRepository clientInfoQueryRepository;

    @Override
    @Deprecated
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    @Deprecated
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientInfo clientInfo = clientInfoQueryRepository.findByClientId(clientId)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        return generateRegisteredClient(clientId, clientInfo);
    }

    private RegisteredClient generateRegisteredClient(String clientId, ClientInfo clientInfo) {
        String id = UUID.randomUUID().toString();
        List<String> scopes = clientInfo.getScope();

        RegisteredClient.Builder builder = RegisteredClient.withId(id)
                .clientId(clientId)
                .clientSecret(clientInfo.getClientSecret())
                .clientName(clientInfo.getClientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(clientInfo.getRegisteredRedirectUri());

        for (String scope : scopes) {
            builder.scope(scope);
        }

        return builder.build();
    }
}
