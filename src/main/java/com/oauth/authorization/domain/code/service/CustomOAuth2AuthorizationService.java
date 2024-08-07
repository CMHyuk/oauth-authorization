package com.oauth.authorization.domain.code.service;

import com.oauth.authorization.domain.code.exception.OAuthAuthorizationCodeErrorCode;
import com.oauth.authorization.domain.code.model.OAuth2Authorization;
import com.oauth.authorization.domain.code.repository.OAuthAuthorizationQueryRepository;
import com.oauth.authorization.domain.code.repository.OAuthAuthorizationRepository;
import com.oauth.authorization.domain.user.model.UserInfoAdapter;
import com.oauth.authorization.domain.user.service.UserInfoService;
import com.oauth.authorization.global.exception.BusinessException;
import com.oauth.authorization.global.util.SerializableObjectConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private static final String INITIAL_CODE = "EMPTY_CODE";

    private final OAuthAuthorizationQueryRepository oAuthAuthorizationQueryRepository;
    private final OAuthAuthorizationRepository oAuthAuthorizationRepository;
    private final UserInfoService userInfoService;

    @Override
    public void save(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        String tenantId = getTenantId(authorization);
        if (!isComplete(authorization)) {
            handleIncompleteAuthorization(authorization, tenantId);
        } else {
            handleCompleteAuthorization(authorization, tenantId);
        }
    }

    @Override
    public void remove(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        String authorizationId = authorization.getId();
        String tenantId = getTenantId(authorization);
        oAuthAuthorizationQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresent(oAuth2Authorization -> oAuthAuthorizationRepository.delete(tenantId, oAuth2Authorization));
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findById(String id) {
        OAuth2Authorization oAuth2Authorization = oAuthAuthorizationQueryRepository.findByAuthorizationId(id)
                .orElseThrow(() -> BusinessException.from(OAuthAuthorizationCodeErrorCode.NOT_FOUND));
        return SerializableObjectConverter.deserialize(oAuth2Authorization.getAuthorization());
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        OAuth2Authorization oAuth2Authorization = oAuthAuthorizationQueryRepository.findByToken(token, tokenType.getValue())
                .orElseThrow(() -> BusinessException.from(OAuthAuthorizationCodeErrorCode.NOT_FOUND));
        return SerializableObjectConverter.deserialize(oAuth2Authorization.getAuthorization());
    }

    private boolean isComplete(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private void handleIncompleteAuthorization(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        oAuthAuthorizationQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresentOrElse(
                        oAuth2Authorization -> updateOAuth2Authorization(authorization, tenantId, oAuth2Authorization),
                        () -> saveNewOAuth2Authorization(authorization, tenantId, authorizationId)
                );
    }

    private void saveNewOAuth2Authorization(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization, String tenantId, String authorizationId) {
        OAuth2Authorization newOAuth2Authorization = OAuth2Authorization.create(
                INITIAL_CODE,
                authorization.getAttribute("state"),
                authorizationId,
                SerializableObjectConverter.serialize(authorization));
        oAuthAuthorizationRepository.save(tenantId, newOAuth2Authorization);
    }

    private void handleCompleteAuthorization(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        oAuthAuthorizationQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresent(oAuth2Authorization -> updateOAuth2Authorization(authorization, tenantId, oAuth2Authorization));
    }

    private void updateOAuth2Authorization(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization, String tenantId, OAuth2Authorization oAuth2Authorization) {
        String code = getCode(authorization);
        oAuth2Authorization.updateAuthorization(code, SerializableObjectConverter.serialize(authorization));
        oAuthAuthorizationRepository.save(tenantId, oAuth2Authorization);
    }

    private String getCode(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode.getToken().getTokenValue();
    }

    private String getTenantId(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        String username = authorization.getPrincipalName();
        UserInfoAdapter userInfoAdapter = (UserInfoAdapter) userInfoService.loadUserByUsername(username);
        return userInfoAdapter.getUserInfo().getTenantId();
    }
}
