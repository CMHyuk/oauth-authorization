package com.oauth.authorization.domain.token.model;

import com.oauth.authorization.global.util.References;
import com.oauth.authorization.global.util.SourceIpUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CLIENT_DETAILS + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
@Mapping(mappingPath = "access_token_mapping.json")
public class ElasticSearchToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String authorizationId;
    private String username;
    private String accessToken;
    private String refreshToken;
    private String ipAddress;

    public static ElasticSearchToken from(OAuth2Authorization authorization) {
        return new ElasticSearchToken(
                authorization.getId(),
                authorization.getPrincipalName(),
                authorization.getAccessToken().getToken().getTokenValue(),
                authorization.getRefreshToken().getToken().getTokenValue()
        );
    }

    public ElasticSearchToken(String authorizationId, String username, String accessToken, String refreshToken) {
        this.authorizationId = authorizationId;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        this.ipAddress = SourceIpUtil.extractSourceIpFrom(attributes.getRequest());
    }
}
