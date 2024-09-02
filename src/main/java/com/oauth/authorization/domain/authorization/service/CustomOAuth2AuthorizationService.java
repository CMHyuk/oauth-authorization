package com.oauth.authorization.domain.authorization.service;

import com.oauth.authorization.domain.authorization.exception.OAuth2AuthorizationErrorCode;
import com.oauth.authorization.domain.authorization.mapper.CustomOAuth2AuthorizationMapper;
import com.oauth.authorization.domain.authorization.model.CustomOAuth2Authorization;
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

    private final CustomOAuth2AuthorizationRepository customOAuth2AuthorizationRepository;
    private final CustomOAuth2AuthorizationMapper customOAuth2AuthorizationMapper;
    private final ElasticSearchTokenService elasticSearchTokenService;
    private final UserInfoService userInfoService;

    /**
     * 1. 최초 로그인 - state만 생성해 OAuth2Authorization save()
     * 2. 동의 항목 체크 - code를 생성하고 state는 삭제한 후 OAuth2Authorization 에 code를 담아 save()
     * 3. accessToken, refreshToken발급 - 토큰을 생성해 OAuth2Authorization에 담아 save()호출
     * 4. 재 로그인 - state는 생성하지 않고, 최초 로그인 시 동의 항목 모두 체크했으면 바로 code 생성하고 save() 호출, 모두 체크하지 않았다면 최초 로그인과 같은 방식으로 code 생성 후 save()
     * 5. 토큰 재발급 - 토큰 생성 후 save()
     */
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
        customOAuth2AuthorizationRepository.findByAuthorizationId(authorizationId)
                .ifPresent(oAuth2Authorization -> customOAuth2AuthorizationRepository.delete(tenantId, oAuth2Authorization));
    }

    @Override
    public OAuth2Authorization findById(String id) {
        CustomOAuth2Authorization oAuth2Authorization = customOAuth2AuthorizationRepository.findByAuthorizationId(id)
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
        return oAuth2Authorization.getOAuth2Authorization();
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        CustomOAuth2Authorization oAuth2Authorization = customOAuth2AuthorizationRepository.findByToken(token, tokenType.getValue())
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
        return oAuth2Authorization.getOAuth2Authorization();
    }

    private boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private void handleIncompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        customOAuth2AuthorizationRepository.findByAuthorizationId(authorizationId)
                .ifPresentOrElse(
                        oAuth2Authorization -> updateOAuth2Authorization(authorization, tenantId, oAuth2Authorization),
                        () -> saveNewOAuth2Authorization(authorization, tenantId, authorizationId)
                );
    }

    private void saveNewOAuth2Authorization(OAuth2Authorization authorization, String tenantId, String authorizationId) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> token = authorization.getToken(OAuth2AuthorizationCode.class);
        CustomOAuth2Authorization newCustomOAuth2Authorization = customOAuth2AuthorizationMapper.create(
                token,
                authorization.getAttribute("state"),
                authorizationId,
                authorization
        );
        customOAuth2AuthorizationRepository.save(tenantId, newCustomOAuth2Authorization);
    }

    private void handleCompleteAuthorization(OAuth2Authorization authorization, String tenantId) {
        String authorizationId = authorization.getId();
        CustomOAuth2Authorization customOAuth2Authorization = customOAuth2AuthorizationRepository.findByAuthorizationId(authorizationId)
                .orElseThrow(() -> BusinessException.from(OAuth2AuthorizationErrorCode.NOT_FOUND));
        elasticSearchTokenService.save(customOAuth2Authorization, authorization, tenantId);
        updateOAuth2Authorization(authorization, tenantId, customOAuth2Authorization);
    }

    private void updateOAuth2Authorization(OAuth2Authorization authorization, String tenantId, CustomOAuth2Authorization oAuth2Authorization) {
        String code = getCode(authorization);
        oAuth2Authorization.updateAuthorization(code, authorization);
        customOAuth2AuthorizationRepository.save(tenantId, oAuth2Authorization);
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
