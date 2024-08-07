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

    private static final String ID_KEYWORD = "_id";
    private static final String STATE_KEYWORD = "state.keyword";
    private static final String CODE_KEYWORD = "code.keyword";
    private static final String ACCESS_TOKEN_KEYWORD = "tokenValue.keyword";
    private static final String AUTHORIZATION_KEYWORD = "authorizationId.keyword";

    private final OAuthAuthorizationCodeRepository oauthAuthorizationCodeRepository;

    public Optional<OAuthAuthorizationCode> findById(String id) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ID_KEYWORD, id));
        OAuthAuthorizationCode oAuthAuthorizationCode = oauthAuthorizationCodeRepository.find(null, query);
        return Optional.ofNullable(oAuthAuthorizationCode);
    }

    public Optional<OAuthAuthorizationCode> findByAuthorizationId(String authorizationId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(AUTHORIZATION_KEYWORD, authorizationId));
        OAuthAuthorizationCode oAuthAuthorizationCode = oauthAuthorizationCodeRepository.find(null, query);
        return Optional.ofNullable(oAuthAuthorizationCode);
    }

    public Optional<OAuthAuthorizationCode> findByToken(String token, String tokenType) {
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
        OAuthAuthorizationCode oAuthAuthorizationCode = oauthAuthorizationCodeRepository.find(null, query);
        return Optional.ofNullable(oAuthAuthorizationCode);
    }

    public Optional<OAuthAuthorizationCode> findByCode(String code) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(CODE_KEYWORD, code));
        OAuthAuthorizationCode oAuthAuthorizationCode = oauthAuthorizationCodeRepository.find(null, query);
        return Optional.ofNullable(oAuthAuthorizationCode);
    }
}
