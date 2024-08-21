package com.oauth.authorization.domain.token.exception;

import com.oauth.authorization.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum TokenErrorCode implements ErrorCode {

    NOT_FOUND(5001, 404, "존재하지 않는 토큰입니다.");

    private final int code;
    private final int httpStatusCode;
    private final String message;
}
