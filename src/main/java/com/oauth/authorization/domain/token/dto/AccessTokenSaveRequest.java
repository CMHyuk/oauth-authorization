package com.oauth.authorization.domain.token.dto;

public record AccessTokenSaveRequest(String code, String redirectUrl, String grantType) {
}
