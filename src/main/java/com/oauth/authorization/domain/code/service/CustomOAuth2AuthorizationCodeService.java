package com.oauth.authorization.domain.code.service;

import com.oauth.authorization.domain.code.exception.OAuthAuthorizationCodeErrorCode;
import com.oauth.authorization.domain.code.model.OAuthAuthorizationCode;
import com.oauth.authorization.domain.code.repository.OAuthAuthorizationCodeQueryRepository;
import com.oauth.authorization.domain.code.repository.OAuthAuthorizationCodeRepository;
import com.oauth.authorization.domain.user.model.UserInfoAdapter;
import com.oauth.authorization.domain.user.service.UserInfoService;
import com.oauth.authorization.global.exception.BusinessException;
import com.oauth.authorization.global.util.SerializableObjectConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationCodeService implements OAuth2AuthorizationService {

    private static final String INITIAL_CODE = "EMPTY_CODE";

    private final OAuthAuthorizationCodeQueryRepository oAuthAuthorizationCodeQueryRepository;
    private final OAuthAuthorizationCodeRepository oAuthAuthorizationCodeRepository;
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
        oAuthAuthorizationCodeQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresent(oAuthAuthorizationCode -> oAuthAuthorizationCodeRepository.delete(tenantId, oAuthAuthorizationCode));
    }

    @Override
    public OAuth2Authorization findById(String id) {
        OAuthAuthorizationCode oAuthAuthorizationCode = oAuthAuthorizationCodeQueryRepository.findByAuthorizationId(id)
                .orElseThrow(() -> BusinessException.from(OAuthAuthorizationCodeErrorCode.NOT_FOUND));
        return SerializableObjectConverter.deserialize(oAuthAuthorizationCode.getAuthorization());
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        OAuthAuthorizationCode oAuthAuthorizationCode = oAuthAuthorizationCodeQueryRepository.findByToken(token, tokenType.getValue())
                .orElseThrow(() -> BusinessException.from(OAuthAuthorizationCodeErrorCode.NOT_FOUND));
        return SerializableObjectConverter.deserialize(oAuthAuthorizationCode.getAuthorization());
    }

    private boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private void handleIncompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        oAuthAuthorizationCodeQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresentOrElse(
                        oAuthAuthorizationCode -> {
                            String code = getCode(authorization);
                            oAuthAuthorizationCode.updateAuthorization(code, SerializableObjectConverter.serialize(authorization));
                            oAuthAuthorizationCodeRepository.save(tenantId, oAuthAuthorizationCode);
                        },
                        () -> {
                            OAuthAuthorizationCode newOAuthAuthorizationCode = OAuthAuthorizationCode.create(
                                    INITIAL_CODE,
                                    authorization.getAttribute("state"),
                                    authorizationId,
                                    SerializableObjectConverter.serialize(authorization));
                            oAuthAuthorizationCodeRepository.save(tenantId, newOAuthAuthorizationCode);
                        }
                );
    }

    private void handleCompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        oAuthAuthorizationCodeQueryRepository.findByAuthorizationId(authorizationId)
                .ifPresent(oAuthAuthorizationCode -> {
                    String code = getCode(authorization);
                    oAuthAuthorizationCode.updateAuthorization(code, SerializableObjectConverter.serialize(authorization));
                    oAuthAuthorizationCodeRepository.save(tenantId, oAuthAuthorizationCode);
                });
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
