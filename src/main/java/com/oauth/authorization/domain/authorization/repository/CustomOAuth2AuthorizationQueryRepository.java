package com.oauth.authorization.domain.authorization.repository;

import com.oauth.authorization.domain.authorization.exception.OAuth2AuthorizationErrorCode;
import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import com.oauth.authorization.domain.token.exception.TokenErrorCode;
import com.oauth.authorization.domain.token.model.ElasticSearchToken;
import com.oauth.authorization.domain.token.repository.ElasticSearchTokenQueryRepository;
import com.oauth.authorization.global.exception.BusinessException;
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
    private static final String AUTHORIZATION_KEYWORD = "authorizationId.keyword";

    private final CustomOAuth2AuthorizationRepository oauthAuthorizationRepository;
    private final ElasticSearchTokenQueryRepository elasticSearchTokenQueryRepository;

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
        if (tokenType.equals("refresh_token")) {
            CustomOAuth2Authorization customOAuth2Authorization = findByRefreshToken(token);
            return Optional.ofNullable(customOAuth2Authorization);
        }
        CustomOAuth2Authorization customOAuth2Authorization = oauthAuthorizationRepository.find(null, query);
        return Optional.ofNullable(customOAuth2Authorization);
    }

    private CustomOAuth2Authorization findByRefreshToken(String token) {
        ElasticSearchToken elasticSearchToken = elasticSearchTokenQueryRepository.findByRefreshToken(token)
                .orElseThrow(() -> BusinessException.from(TokenErrorCode.NOT_FOUND));
        String clientId = elasticSearchToken.getAuthorizationId();
        return findByAuthorizationId(clientId)
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
    }
}
