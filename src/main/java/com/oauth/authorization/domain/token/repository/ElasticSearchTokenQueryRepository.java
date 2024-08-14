package com.oauth.authorization.domain.token.repository;

import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ElasticSearchTokenQueryRepository {

    private final ElasticSearchTokenRepository elasticSearchTokenRepository;

    private static final String REFRESH_TOKEN_KEYWORD = "refreshToken.keyword";

    public Optional<ElasticSearchToken> findByRefreshToken(String refreshToken) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.matchQuery(REFRESH_TOKEN_KEYWORD, refreshToken));
        ElasticSearchToken elasticSearchToken = elasticSearchTokenRepository.find(null, query);
        return Optional.ofNullable(elasticSearchToken);
    }
}
