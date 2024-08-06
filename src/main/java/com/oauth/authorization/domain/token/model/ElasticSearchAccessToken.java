package com.oauth.authorization.domain.token.model;

import com.oauth.authorization.global.util.References;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CLIENT_DETAILS + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
public class ElasticSearchAccessToken {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String accessToken;
    private String refreshToken;
    private String ipAddress;

    public static ElasticSearchAccessToken create(String token, String refreshToken, String ipAddress) {
        return new ElasticSearchAccessToken(token, refreshToken, ipAddress);
    }

    public ElasticSearchAccessToken(String accessToken, String refreshToken, String ipAddress) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.ipAddress = ipAddress;
    }
}
