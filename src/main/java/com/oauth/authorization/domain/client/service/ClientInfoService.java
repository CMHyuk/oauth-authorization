package com.oauth.authorization.domain.client.service;

import com.oauth.authorization.domain.client.exception.ClientErrorCode;
import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.domain.client.model.ClientInfoAdapter;
import com.oauth.authorization.domain.client.repository.ClientInfoQueryRepository;
import com.oauth.authorization.domain.client.repository.ClientInfoRepository;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientInfoService implements RegisteredClientRepository {

    private final ClientInfoQueryRepository clientInfoQueryRepository;
    private final ClientInfoRepository clientInfoRepository;

    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientInfo clientInfo = clientInfoQueryRepository.findByClientId(clientId)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        return new ClientInfoAdapter(clientInfo);
    }
}
