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

    private static final String USERNAME_KEYWORD = "username.keyword";

    private final ElasticSearchTokenRepository elasticSearchTokenRepository;

    public Optional<ElasticSearchToken> findByUsername(String username) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(USERNAME_KEYWORD, username));
        ElasticSearchToken elasticSearchToken = elasticSearchTokenRepository.find(null, query);
        return Optional.ofNullable(elasticSearchToken);
    }
}
