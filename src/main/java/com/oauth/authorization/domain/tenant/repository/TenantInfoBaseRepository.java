package com.oauth.authorization.domain.tenant.repository;


import com.oauth.authorization.domain.tenant.model.TenantInfo;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface TenantInfoBaseRepository extends CustomAwareRepository<TenantInfo, String> {
}
