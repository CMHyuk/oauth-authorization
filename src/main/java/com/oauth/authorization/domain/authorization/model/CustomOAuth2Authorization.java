package com.oauth.authorization.domain.authorization.model;

import com.oauth.authorization.global.domain.BaseEntity;
import com.oauth.authorization.global.util.References;
import com.oauth.authorization.global.util.SerializableObjectConverter;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CODE + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
public class CustomOAuth2Authorization extends BaseEntity {

    private String code;
    private String state;
    private String authorizationId;
    private String oAuth2Authorization;

    public CustomOAuth2Authorization(String code, String state, String authorizationId, OAuth2Authorization oAuth2Authorization) {
        this.code = code;
        this.state = state;
        this.authorizationId = authorizationId;
        this.oAuth2Authorization = SerializableObjectConverter.serialize(oAuth2Authorization);
    }

    public void updateAuthorization(String code, OAuth2Authorization authorization) {
        this.code = code;
        this.oAuth2Authorization = SerializableObjectConverter.serialize(authorization);
    }

    public OAuth2Authorization getOAuth2Authorization() {
        return SerializableObjectConverter.deserialize(oAuth2Authorization);
    }
}
