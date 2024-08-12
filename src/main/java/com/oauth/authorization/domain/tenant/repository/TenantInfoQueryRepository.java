package com.oauth.authorization.domain.tenant.repository;

import com.oauth.authorization.domain.tenant.model.TenantInfo;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TenantInfoQueryRepository {

    private static final String ID_KEYWORD = "_id";

    private final TenantInfoRepository tenantInfoRepository;

    public Optional<TenantInfo> findByTenantId(String tenantName) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ID_KEYWORD, tenantName));
        TenantInfo tenantInfo = tenantInfoRepository.find(null, query);
        return Optional.ofNullable(tenantInfo);
    }
}
