package com.oauth.authorization.domain.token.repository;

import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ElasticSearchTokenRepository {

    private static final String REFRESH_TOKEN_KEYWORD = "refreshToken.keyword";

    private final ElasticSearchTokenBaseRepository elasticSearchTokenBaseRepository;

    public void save(String tenantId, ElasticSearchToken elasticSearchToken) {
        elasticSearchTokenBaseRepository.save(tenantId, elasticSearchToken);
    }

    public Optional<ElasticSearchToken> findByRefreshToken(String refreshToken) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(REFRESH_TOKEN_KEYWORD, refreshToken));
        ElasticSearchToken elasticSearchToken = elasticSearchTokenBaseRepository.find(null, query);
        return Optional.ofNullable(elasticSearchToken);
    }
}
