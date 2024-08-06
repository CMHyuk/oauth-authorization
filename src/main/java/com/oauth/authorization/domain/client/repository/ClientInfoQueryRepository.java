package com.oauth.authorization.domain.client.repository;

import com.oauth.authorization.domain.client.model.ClientInfo;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientInfoQueryRepository {

    private static final String CLIENT_ID_KEYWORD = "clientId.keyword";
    private static final String ID_KEYWORD = "_id";

    private final ClientInfoRepository clientInfoRepository;

    public Optional<ClientInfo> findById(String id) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ID_KEYWORD, id));
        ClientInfo clientInfo = clientInfoRepository.find(null, query);
        return Optional.ofNullable(clientInfo);
    }

    public Optional<ClientInfo> findByClientId(String clientId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(CLIENT_ID_KEYWORD, clientId));
        ClientInfo clientInfo = clientInfoRepository.find(null, query);
        return Optional.ofNullable(clientInfo);
    }
}
