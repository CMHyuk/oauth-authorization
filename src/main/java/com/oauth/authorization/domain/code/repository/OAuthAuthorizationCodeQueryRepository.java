package com.oauth.authorization.domain.code.repository;

import com.oauth.authorization.domain.code.model.OAuthAuthorizationCode;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OAuthAuthorizationCodeQueryRepository {

    private static final String ID_KEYWORD = "id.keyword";

    private final OAuthAuthorizationCodeRepository oauthAuthorizationCodeRepository;

    public Optional<OAuthAuthorizationCode> findById(String id) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ID_KEYWORD, id));
        OAuthAuthorizationCode oAuthAuthorizationCode = oauthAuthorizationCodeRepository.find(null, query);
        return Optional.ofNullable(oAuthAuthorizationCode);
    }
}
