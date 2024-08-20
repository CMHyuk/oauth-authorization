package com.oauth.authorization.domain.user.model;

import com.oauth.authorization.global.domain.BaseEntity;
import com.oauth.authorization.global.util.References;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Document(indexName = References.ELASTIC_INDEX_PREFIX_OAUTH_USER + "*", createIndex = false)
@SuppressWarnings("JpaAttributeTypeInspection")
public class UserInfo extends BaseEntity {

    private String tenantId;
    private String username;
    private String userId;
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Set<UserRole> role;
}
