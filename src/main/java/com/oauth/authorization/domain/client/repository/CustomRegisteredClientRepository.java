package com.oauth.authorization.domain.client.repository;

import com.oauth.authorization.domain.client.exception.ClientErrorCode;
import com.oauth.authorization.domain.client.mapper.RegisteredClientMapper;
import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientInfoRepository clientInfoRepository;
    private final RegisteredClientMapper registeredClientMapper;

    @Override
    @Deprecated
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
        ClientInfo clientInfo = clientInfoRepository.findById(id)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        return registeredClientMapper.toRegisteredClient(clientInfo);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientInfo clientInfo = clientInfoRepository.findByClientId(clientId)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        return registeredClientMapper.toRegisteredClient(clientInfo);
    }
}
