package com.oauth.authorization.domain.token.repository;

import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import com.oauth.authorization.elasticsearch.base.CustomAwareRepository;

public interface ElasticSearchTokenRepository extends CustomAwareRepository<ElasticSearchToken, String> {
}
