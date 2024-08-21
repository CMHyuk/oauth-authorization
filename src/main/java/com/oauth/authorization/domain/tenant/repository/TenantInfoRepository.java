package com.oauth.authorization.domain.tenant.repository;

import com.oauth.authorization.domain.tenant.model.TenantInfo;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TenantInfoRepository {

    private static final String ID_KEYWORD = "_id";

    private final TenantInfoBaseRepository tenantInfoBaseRepository;

    public Optional<TenantInfo> findByTenantId(String tenantId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ID_KEYWORD, tenantId));
        TenantInfo tenantInfo = tenantInfoBaseRepository.find(null, query);
        return Optional.ofNullable(tenantInfo);
    }
}
