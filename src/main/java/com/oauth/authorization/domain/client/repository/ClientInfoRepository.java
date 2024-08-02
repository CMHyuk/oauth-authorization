package com.oauth.authorization.domain.client.repository;


import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface ClientInfoRepository extends CustomAwareRepository<ClientInfo, String> {
}
