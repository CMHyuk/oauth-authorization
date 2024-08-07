package com.oauth.authorization.domain.code.service;

import com.oauth.authorization.domain.client.exception.ClientErrorCode;
import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.domain.client.repository.ClientInfoQueryRepository;
import com.oauth.authorization.domain.code.exception.OAuthAuthorizationCodeErrorCode;
import com.oauth.authorization.domain.code.model.OAuthAuthorizationCode;
import com.oauth.authorization.domain.code.repository.OAuthAuthorizationCodeQueryRepository;
import com.oauth.authorization.domain.code.repository.OAuthAuthorizationCodeRepository;
import com.oauth.authorization.domain.user.model.UserInfoAdapter;
import com.oauth.authorization.domain.user.service.UserInfoService;
import com.oauth.authorization.global.exception.BusinessException;
import com.oauth.authorization.global.exception.InternalServerErrorCode;
import com.oauth.authorization.global.util.SerializableObjectConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationCodeService implements OAuth2AuthorizationService {

    private final OAuthAuthorizationCodeQueryRepository oAuthAuthorizationCodeQueryRepository;
    private final OAuthAuthorizationCodeRepository oAuthAuthorizationCodeRepository;
    private final ClientInfoQueryRepository clientInfoRepository;
    private final HttpServletResponse httpServletResponse;
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
        String code = getCode(authorization);
        String tenantId = getTenantId(authorization);
        oAuthAuthorizationCodeQueryRepository.findByCode(code)
                .ifPresent(oAuthAuthorizationCode -> oAuthAuthorizationCodeRepository.delete(tenantId, oAuthAuthorizationCode));
    }

    @Override
    public OAuth2Authorization findById(String id) {
        OAuthAuthorizationCode oAuthAuthorizationCode = oAuthAuthorizationCodeQueryRepository.findById(id)
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
        String code = generateAuthorizationCode();
        ClientInfo clientInfo = getClientInfo(authorization.getRegisteredClientId());
        String redirectUri = clientInfo.getRegisteredRedirectUris().iterator().next();
        String state = authorization.getAttribute("state");

        OAuth2Authorization updatedAuthorization = OAuth2Authorization.from(authorization)
                .token(new OAuth2AuthorizationCode(code, Instant.now(), Instant.now().plus(5, ChronoUnit.MINUTES)))
                .build();

        OAuthAuthorizationCode oAuthAuthorizationCode = OAuthAuthorizationCode.create(
                code,
                state,
                SerializableObjectConverter.serialize(updatedAuthorization)
        );

        oAuthAuthorizationCodeRepository.save(tenantId, oAuthAuthorizationCode);
        redirectToClient(redirectUri, code, state);
    }

    private void handleCompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String code = getCode(authorization);
        oAuthAuthorizationCodeQueryRepository.findByCode(code)
                .ifPresent(oAuthAuthorizationCode -> {
                    oAuthAuthorizationCode.updateAuthorization(SerializableObjectConverter.serialize(authorization));
                    oAuthAuthorizationCodeRepository.save(tenantId, oAuthAuthorizationCode);
                });
    }

    private String getCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode.getToken().getTokenValue();
    }

    private void redirectToClient(String redirectUri, String code, String state) {
        try {
            String redirectUrl = String.format("%s?code=%s&state=%s", redirectUri, code, state);
            httpServletResponse.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
        }
    }

    private ClientInfo getClientInfo(String id) {
        return clientInfoRepository.findById(id)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
    }

    private String generateAuthorizationCode() {
        String charset = "!\"#$%&'()*+,-./";
        String randomString = RandomStringUtils.random(32, charset);
        return new String(Base64.encodeBase64(randomString.getBytes())).replace("=", "");
    }

    private String getTenantId(OAuth2Authorization authorization) {
        String username = authorization.getPrincipalName();
        UserInfoAdapter userInfoAdapter = (UserInfoAdapter) userInfoService.loadUserByUsername(username);
        return userInfoAdapter.getUserInfo().getTenantId();
    }
}
