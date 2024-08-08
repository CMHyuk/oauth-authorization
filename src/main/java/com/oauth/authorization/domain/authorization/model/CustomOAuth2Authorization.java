package com.oauth.authorization.domain.authorization.model;

import com.oauth.authorization.global.util.References;
import com.oauth.authorization.global.util.SerializableObjectConverter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CODE + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
public class CustomOAuth2Authorization implements Serializable {

    private static final String INITIAL_CODE = "EMPTY_CODE";

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String code;
    private String state;
    private String authorizationId;
    private String oAuth2Authorization;

    public static CustomOAuth2Authorization create(OAuth2Authorization.Token<OAuth2AuthorizationCode> token, String state, String authorizationId, OAuth2Authorization authentication) {
        return new CustomOAuth2Authorization(
                token == null ? INITIAL_CODE : token.getToken().getTokenValue(),
                state,
                authorizationId,
                authentication);
    }

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
