package com.oauth.authorization.domain.code.exception;

import com.oauth.authorization.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum OAuthAuthorizationCodeErrorCode implements ErrorCode {

    NOT_FOUND(4001, 404, "존재하지 않는 OAuth2Authorization입니다.");

    private final int code;
    private final int httpStatusCode;
    private final String message;
}
