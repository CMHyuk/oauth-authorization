package com.oauth.authorization.domain.token.repository;

import com.oauth.authorization.domain.token.model.ElasticSearchAccessToken;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface ElasticSearchAccessTokenRepository extends CustomAwareRepository<ElasticSearchAccessToken, String> {
}
