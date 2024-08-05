package com.oauth.authorization.domain.token.service;

import com.oauth.authorization.domain.token.dto.OAuth2TokenResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "oauth2TokenProvider", url = "http://localhost:9000")
public interface OAuth2TokenProviderApi {

    @PostMapping(value = "/oauth2/token", consumes = "application/x-www-form-urlencoded")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    ResponseEntity<OAuth2TokenResponse> getToken(@RequestBody Map<String, ?> formParams);
}
