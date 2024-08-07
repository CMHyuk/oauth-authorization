package com.oauth.authorization.domain.code.model;

import com.oauth.authorization.global.util.References;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CODE)
@Setting(settingPath = "lower_case_normalizer_setting.json")
public class OAuthAuthorizationCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String code;
    private String state;
    private String authorization;

    public static OAuthAuthorizationCode create(String code, String state, String authentication) {
        return new OAuthAuthorizationCode(code, state, authentication);
    }

    public OAuthAuthorizationCode(String code, String state, String authorization) {
        this.code = code;
        this.state = state;
        this.authorization = authorization;
    }

    public void updateAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
