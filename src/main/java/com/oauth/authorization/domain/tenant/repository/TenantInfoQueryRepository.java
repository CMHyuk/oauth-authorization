package com.oauth.authorization.domain.tenant.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TenantInfoQueryRepository {

    private static final String TENANT_NAME_KEYWORD = "tenantName.keyword";

    private final TenantInfoRepository tenantInfoRepository;


}
