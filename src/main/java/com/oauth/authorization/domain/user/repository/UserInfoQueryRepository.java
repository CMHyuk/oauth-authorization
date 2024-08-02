package com.oauth.authorization.domain.user.repository;

import com.oauth.authorization.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserInfoQueryRepository {

    private static final String USER_ID_KEYWORD = "userId.keyword";

    private final UserInfoRepository userInfoRepository;

    public Optional<UserInfo> findByUserId(String userId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(USER_ID_KEYWORD, userId));
        return Optional.ofNullable(userInfoRepository.find(null, query));
    }
}
