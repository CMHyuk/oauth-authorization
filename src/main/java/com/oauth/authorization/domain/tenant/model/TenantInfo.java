package com.oauth.authorization.domain.tenant.model;

import com.oauth.authorization.global.exception.BusinessException;
import com.oauth.authorization.global.util.References;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

import static com.oauth.authorization.domain.tenant.exception.TenantErrorCode.INVALID_MASTER_TENANT_NAME_REQUEST;


@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_COMPANY + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
@SuppressWarnings("squid:S1948")
public class TenantInfo {

    private static final String MASTER_TENANT = "master";

    @Id
    private String id;

    private String tenantName;
    private byte[] tenantRSAPrivateKey;
    private byte[] tenantRSAPublicKey;

    public static TenantInfo createMasterTenant(String tenantName, byte[] tenantRSAPrivateKey, byte[] tenantRSAPublicKey) {
        if (!tenantName.equals(MASTER_TENANT)) {
            throw BusinessException.from(INVALID_MASTER_TENANT_NAME_REQUEST);
        }
        return new TenantInfo(MASTER_TENANT, tenantRSAPrivateKey, tenantRSAPublicKey);
    }

    public TenantInfo(String tenantName, byte[] tenantRSAPrivateKey, byte[] tenantRSAPublicKey) {
        this.tenantName = tenantName;
        this.tenantRSAPrivateKey = tenantRSAPrivateKey;
        this.tenantRSAPublicKey = tenantRSAPublicKey;
    }
}
