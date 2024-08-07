package com.oauth.authorization.domain.code.repository;

import com.oauth.authorization.domain.code.model.OAuth2Authorization;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OAuthAuthorizationQueryRepository {

    private static final String STATE_KEYWORD = "state.keyword";
    private static final String CODE_KEYWORD = "code.keyword";
    private static final String ACCESS_TOKEN_KEYWORD = "tokenValue.keyword";
    private static final String AUTHORIZATION_KEYWORD = "authorizationId.keyword";

    private final OAuthAuthorizationRepository oauthAuthorizationRepository;

    public Optional<OAuth2Authorization> findByAuthorizationId(String authorizationId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(AUTHORIZATION_KEYWORD, authorizationId));
        OAuth2Authorization oAuth2Authorization = oauthAuthorizationRepository.find(null, query);
        return Optional.ofNullable(oAuth2Authorization);
    }

    public Optional<OAuth2Authorization> findByToken(String token, String tokenType) {
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
        OAuth2Authorization oAuth2Authorization = oauthAuthorizationRepository.find(null, query);
        return Optional.ofNullable(oAuth2Authorization);
    }
}
