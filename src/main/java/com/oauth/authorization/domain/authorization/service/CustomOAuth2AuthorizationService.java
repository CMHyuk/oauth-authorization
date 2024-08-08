package com.oauth.authorization.domain.authorization.service;

import com.oauth.authorization.domain.authorization.exception.OAuth2AuthorizationErrorCode;
import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
import com.oauth.authorization.domain.authorization.repository.CustomOAuth2AuthorizationQueryRepository;
import com.oauth.authorization.domain.authorization.repository.CustomOAuth2AuthorizationRepository;
import com.oauth.authorization.domain.token.service.ElasticSearchTokenService;
import com.oauth.authorization.domain.user.model.UserInfoAdapter;
import com.oauth.authorization.domain.user.service.UserInfoService;
import com.oauth.authorization.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private static final String INITIAL_CODE = "EMPTY_CODE";

    private final CustomOAuth2AuthorizationQueryRepository oAuth2AuthorizationQueryRepository;
    private final CustomOAuth2AuthorizationRepository oAuthAuthorizationRepository;
    private final ElasticSearchTokenService elasticSearchTokenService;
    private final UserInfoService userInfoService;

    @Override
    public void save(OAuth2Authorization authorization) {
        String tenantId = getTenantId(authorization);
        if (!isComplete(authorization)) {
            handleIncompleteAuthorization(authorization, tenantId);
        } else {
            handleCompleteAuthorization(authorization, tenantId);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        String authorizationId = authorization.getId();
        String tenantId = getTenantId(authorization);
        oAuth2AuthorizationQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresent(oAuth2Authorization -> oAuthAuthorizationRepository.delete(tenantId, oAuth2Authorization));
    }

    @Override
    public OAuth2Authorization findById(String id) {
        CustomOAuth2Authorization oAuth2Authorization = oAuth2AuthorizationQueryRepository.findByAuthorizationId(id)
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
        return oAuth2Authorization.getOAuth2Authorization();
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        CustomOAuth2Authorization oAuth2Authorization = oAuth2AuthorizationQueryRepository.findByToken(token, tokenType.getValue())
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
        return oAuth2Authorization.getOAuth2Authorization();
    }

    private boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private void handleIncompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        oAuth2AuthorizationQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresentOrElse(
                        oAuth2Authorization -> updateOAuth2Authorization(authorization, tenantId, oAuth2Authorization),
                        () -> saveNewOAuth2Authorization(authorization, tenantId, authorizationId)
                );
    }

    private void saveNewOAuth2Authorization(OAuth2Authorization authorization, String tenantId, String authorizationId) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> token = authorization.getToken(OAuth2AuthorizationCode.class);
        CustomOAuth2Authorization newCustomOAuth2Authorization = CustomOAuth2Authorization.create(
                token,
                authorization.getAttribute("state"),
                authorizationId,
                authorization
        );
        oAuthAuthorizationRepository.save(tenantId, newCustomOAuth2Authorization);
    }

    private void handleCompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        CustomOAuth2Authorization customOAuth2Authorization = oAuth2AuthorizationQueryRepository.findByAuthorizationId(authorizationId)
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
        elasticSearchTokenService.save(customOAuth2Authorization, authorization, tenantId);
        updateOAuth2Authorization(authorization, tenantId, customOAuth2Authorization);
    }

    private void updateOAuth2Authorization(OAuth2Authorization authorization, String tenantId, CustomOAuth2Authorization oAuth2Authorization) {
        String code = getCode(authorization);
        oAuth2Authorization.updateAuthorization(code, authorization);
        oAuthAuthorizationRepository.save(tenantId, oAuth2Authorization);
    }

    private String getCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode.getToken().getTokenValue();
    }

    private String getTenantId(OAuth2Authorization authorization) {
        String username = authorization.getPrincipalName();
        UserInfoAdapter userInfoAdapter = (UserInfoAdapter) userInfoService.loadUserByUsername(username);
        return userInfoAdapter.getUserInfo().getTenantId();
    }
}
