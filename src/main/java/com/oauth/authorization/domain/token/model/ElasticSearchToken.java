package com.oauth.authorization.domain.token.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oauth.authorization.global.domain.BaseEntity;
import com.oauth.authorization.global.util.References;
import com.oauth.authorization.global.util.SourceIpUtil;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CLIENT_DETAILS + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
@Mapping(mappingPath = "access_token_mapping.json")
public class ElasticSearchToken extends BaseEntity {

    private String authorizationId;

    @Field(type = FieldType.Date,  format = {}, pattern = References.TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = References.TIME_FORMAT)
    private LocalDateTime expiresAt;

    private String username;
    private String accessToken;
    private String refreshToken;
    private String ipAddress;

    public static ElasticSearchToken from(OAuth2Authorization authorization) {
        return new ElasticSearchToken(
                authorization.getId(),
                LocalDateTime.ofInstant(authorization.getAccessToken().getToken().getExpiresAt(), ZoneId.of("Asia/Seoul")),
                authorization.getPrincipalName(),
                authorization.getAccessToken().getToken().getTokenValue(),
                authorization.getRefreshToken().getToken().getTokenValue()
        );
    }

    public ElasticSearchToken(String authorizationId, LocalDateTime expiresAt, String username, String accessToken, String refreshToken) {
        this.authorizationId = authorizationId;
        this.expiresAt = expiresAt;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        this.ipAddress = SourceIpUtil.extractSourceIpFrom(attributes.getRequest());
    }
}
