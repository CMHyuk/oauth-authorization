package com.oauth.authorization.domain.client.exception;

import com.oauth.authorization.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ClientErrorCode implements ErrorCode {

    NOT_FOUND(2001, 404, "존재하지 않은 클라이언트입니다.");

    private final int code;
    private final int httpStatusCode;
    private final String message;
}
