package com.oauth.authorization.domain.code.service;

import com.oauth.authorization.domain.client.exception.ClientErrorCode;
import com.oauth.authorization.domain.client.mapper.RegisteredClientMapper;
import com.oauth.authorization.domain.client.model.ClientInfo;
import com.oauth.authorization.domain.client.repository.ClientInfoQueryRepository;
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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationCodeService implements OAuth2AuthorizationService {

    private final OAuthAuthorizationCodeQueryRepository oAuthAuthorizationCodeQueryRepository;
    private final OAuthAuthorizationCodeRepository oAuthAuthorizationCodeRepository;
    private final ClientInfoQueryRepository clientInfoRepository;
    private final RegisteredClientMapper registeredClientMapper;
    private final HttpServletResponse httpServletResponse;
    private final UserInfoService userInfoService;

    @Override
    public void save(OAuth2Authorization authorization) {
        String code = generateAuthorizationCode();
        String tenantId = getTenantId(authorization);

        String id = authorization.getRegisteredClientId();
        ClientInfo clientInfo = clientInfoRepository.findById(id)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        String redirectUri = clientInfo.getRegisteredRedirectUris().iterator().next();

        oAuthAuthorizationCodeRepository.save(tenantId, new OAuthAuthorizationCode(code, SerializableObjectConverter.serialize(authorization)));
        try {
            String redirectUrl = redirectUri + "?code=" + code + "&state=" + authorization.getAttribute("state");
            httpServletResponse.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        String id = authorization.getId();
        String tenantId = getTenantId(authorization);
        OAuthAuthorizationCode oAuthAuthorizationCode = oAuthAuthorizationCodeQueryRepository.findById(id)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        oAuthAuthorizationCodeRepository.delete(tenantId, oAuthAuthorizationCode);
    }

    @Override
    @Deprecated
    public OAuth2Authorization findById(String id) {
        ClientInfo clientInfo = clientInfoRepository.findById(id)
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        String redirectUri = clientInfo.getRegisteredRedirectUris().iterator().next();

        return null;
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
//        ClientInfo clientInfo = clientInfoRepository.findById(id)
//                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
//        String redirectUri = clientInfo.getRegisteredRedirectUris().iterator().next();
//
//        return oAuthAuthorizationCodeQueryRepository.findByCode(token)
//                .map(code -> {
//                    return OAuth2Authorization.withRegisteredClient(registeredClientMapper.toRegisteredClient(clientInfo))
//                            .principalName(code.getUsername())
//                            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                            .attribute(OAuth2ParameterNames.REDIRECT_URI, code.getRedirectUri())
//                            .authorizationCode(new OAuth2AuthorizationCode(code.getCode(), code.getIssuedAt(), code.getExpiresAt()))
//                            .build();
//                })
//                .orElse(null);
        return null;
    }

    private OAuth2Authorization convertToOAuth2Authorization(OAuth2Authorization oAuth2Authorization) {
        ClientInfo clientInfo = clientInfoRepository.findById(oAuth2Authorization.getId())
                .orElseThrow(() -> BusinessException.from(ClientErrorCode.NOT_FOUND));
        String redirectUri = clientInfo.getRegisteredRedirectUris().iterator().next();

        return OAuth2Authorization.withRegisteredClient(registeredClientMapper.toRegisteredClient(clientInfo))
                .principalName(oAuth2Authorization.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType("authorization_code"))
                .attribute(OAuth2ParameterNames.REDIRECT_URI, redirectUri)
                .build();
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
