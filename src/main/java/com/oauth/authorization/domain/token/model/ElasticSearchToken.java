package com.oauth.authorization.domain.token.model;

import com.oauth.authorization.global.util.References;
import com.oauth.authorization.global.util.SourceIpUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Entity
@Getter
@NoArgsConstructor
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_CLIENT_DETAILS + "*", createIndex = false)
@Setting(settingPath = "lower_case_normalizer_setting.json")
public class ElasticSearchToken {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String accessToken;
    private String refreshToken;
    private String ipAddress;

    public ElasticSearchToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        this.ipAddress = SourceIpUtil.extractSourceIpFrom(attributes.getRequest());

    }
}
