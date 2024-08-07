package com.oauth.authorization.domain.authorization.repository;

import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationQueryRepository {

    private static final String STATE_KEYWORD = "state.keyword";
    private static final String CODE_KEYWORD = "code.keyword";
    private static final String ACCESS_TOKEN_KEYWORD = "tokenValue.keyword";
    private static final String AUTHORIZATION_KEYWORD = "authorizationId.keyword";

    private final CustomOAuth2AuthorizationRepository oauthAuthorizationRepository;

    public Optional<CustomOAuth2Authorization> findByAuthorizationId(String authorizationId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(AUTHORIZATION_KEYWORD, authorizationId));
        CustomOAuth2Authorization customOAuth2Authorization = oauthAuthorizationRepository.find(null, query);
        return Optional.ofNullable(customOAuth2Authorization);
    }

    public Optional<CustomOAuth2Authorization> findByToken(String token, String tokenType) {
        BoolQueryBuilder query = null;
        if (tokenType.equals("state")) {
            query = QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termQuery(STATE_KEYWORD, token));
        }
        if (tokenType.equals("code")) {
            query = QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termQuery(CODE_KEYWORD, token));
        }
        if (tokenType.equals("access_token")) {
            query = QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termQuery(ACCESS_TOKEN_KEYWORD, token));
        }
        CustomOAuth2Authorization customOAuth2Authorization = oauthAuthorizationRepository.find(null, query);
        return Optional.ofNullable(customOAuth2Authorization);
    }
}
