package com.oauth.authorization.domain.tenant.service;

import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.domain.client.repository.ClientInfoQueryRepository;
import com.oauth.authorization.domain.tenant.dto.KeyResponse;
import com.oauth.authorization.domain.tenant.exception.TenantErrorCode;
import com.oauth.authorization.domain.tenant.model.TenantInfo;
import com.oauth.authorization.domain.tenant.repository.TenantInfoQueryRepository;
import com.oauth.authorization.domain.user.exception.UserErrorCode;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantInfoService {

    private final ClientInfoQueryRepository clientInfoQueryRepository;
    private final TenantInfoQueryRepository tenantInfoQueryRepository;

    public KeyResponse getKey(String clientId) {
        ClientInfo clientInfo = clientInfoQueryRepository.findByClientId(clientId)
                .orElseThrow(() -> BusinessException.from(UserErrorCode.NOT_FOUND));

        TenantInfo tenantInfo = tenantInfoQueryRepository.findByTenantId(clientInfo.getTenantId())
                .orElseThrow(() -> BusinessException.from(TenantErrorCode.NOT_FOUND));
        return new KeyResponse(tenantInfo.getTenantRSAPublicKey(), tenantInfo.getTenantRSAPrivateKey());
    }
}
