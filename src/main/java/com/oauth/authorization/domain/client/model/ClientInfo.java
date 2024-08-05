package com.oauth.authorization.domain.client.model;

import com.oauth.authorization.global.util.References;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.HashSet;
import java.util.Set;

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
    private Integer accessTokenValiditySeconds;

    @ElementCollection
    private Set<String> registeredRedirectUris = new HashSet<>();

    @ElementCollection
    private Set<String> scopes = new HashSet<>();
}
