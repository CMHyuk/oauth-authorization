package com.oauth.authorization.domain.tenant.service;

import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.domain.client.repository.ClientInfoRepository;
import com.oauth.authorization.domain.tenant.dto.KeyResponse;
import com.oauth.authorization.domain.tenant.exception.TenantErrorCode;
import com.oauth.authorization.domain.tenant.model.TenantInfo;
import com.oauth.authorization.domain.tenant.repository.TenantInfoRepository;
import com.oauth.authorization.domain.user.exception.UserErrorCode;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantInfoService {

    private final ClientInfoRepository clientInfoRepository;
    private final TenantInfoRepository tenantInfoRepository;

    public KeyResponse getKey(String clientId) {
        ClientInfo clientInfo = clientInfoRepository.findByClientId(clientId)
                .orElseThrow(() -> BusinessException.from(UserErrorCode.NOT_FOUND));
        TenantInfo tenantInfo = tenantInfoRepository.findByTenantId(clientInfo.getTenantId())
                .orElseThrow(() -> BusinessException.from(TenantErrorCode.NOT_FOUND));
        return new KeyResponse(tenantInfo.getTenantRSAPublicKey(), tenantInfo.getTenantRSAPrivateKey());
    }
}
