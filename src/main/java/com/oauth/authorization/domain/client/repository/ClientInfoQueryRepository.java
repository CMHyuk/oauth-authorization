package com.oauth.authorization.domain.client.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ClientInfoQueryRepository {

    private static final String TENANT_ID_KEYWORD = "tenantId.keyword";
    private static final String CLIENT_ID_KEYWORD = "clientId.keyword";

    private final ClientInfoRepository clientInfoRepository;

}
