package com.oauth.authorization.domain.token.controller;

import com.oauth.authorization.domain.token.dto.AccessTokenSaveResponse;
import com.oauth.authorization.domain.token.model.ElasticSearchAccessToken;
import com.oauth.authorization.domain.token.service.ElasticSearchAccessTokenFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ElasticSearchAccessTokenController {

    private final ElasticSearchAccessTokenFacade elasticSearchAccessTokenFacade;

    @PostMapping("/oauth2/token")
    public ResponseEntity<AccessTokenSaveResponse> save(HttpServletRequest request, @RequestParam String code, @RequestParam String redirectUri) {
        String ipAddress = request.getRemoteAddr();
        ElasticSearchAccessToken response = elasticSearchAccessTokenFacade.save(code, redirectUri, ipAddress);
        return ResponseEntity.ok(AccessTokenSaveResponse.from(response));
    }
}
