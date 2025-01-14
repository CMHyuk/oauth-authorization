package com.oauth.authorization.domain.tenant.model;

import com.oauth.authorization.global.domain.BaseEntity;
import com.oauth.authorization.global.util.References;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_COMPANY + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
@SuppressWarnings("squid:S1948")
public class TenantInfo extends BaseEntity {

    private String tenantName;
    private byte[] tenantRSAPrivateKey;
    private byte[] tenantRSAPublicKey;
}
