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

    private String tokenValue;
    private String code;
    private String state;
    private String authentication;

    public static OAuthAuthorizationCode create(String token, String code, String state, String authentication) {
        return new OAuthAuthorizationCode(token, code, state, authentication);
    }

    public OAuthAuthorizationCode(String tokenValue, String code, String state, String authentication) {
        this.tokenValue = tokenValue;
        this.code = code;
        this.state = state;
        this.authentication = authentication;
    }
}
