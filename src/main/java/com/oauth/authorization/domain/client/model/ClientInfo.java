package com.oauth.authorization.domain.client.model;

import com.oauth.authorization.global.util.References;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CLIENT + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
@SuppressWarnings("squid:S1948")
public class ClientInfo {

    private static final Integer VALIDITY_SECONDS = 300;

    @Id
    private String id;

    private String tenantId;
    private String clientName;
    private String clientId;
    private String clientSecret;
    private String registeredRedirectUri;
    private Integer accessTokenValiditySeconds;

    @ElementCollection
    private List<String> scope = new ArrayList<>();

    public ClientInfo(String tenantId, String clientName, String clientId, String clientSecret, String registeredRedirectUri, List<String> scope) {
        this.tenantId = tenantId;
        this.clientName = clientName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.registeredRedirectUri = registeredRedirectUri;
        this.scope = scope;
        this.accessTokenValiditySeconds = VALIDITY_SECONDS;
    }

    public void update(String clientName, String clientId, String clientSecret, String registeredRedirectUri, Integer accessTokenValiditySeconds, List<String> scope) {
        this.clientName = clientName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.registeredRedirectUri = registeredRedirectUri;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.scope = scope;
    }
}
