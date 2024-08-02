package com.oauth.authorization.global.exception;

import lombok.Getter;

@Getter
public class HttpMessageNotReadableErrorCode implements ErrorCode {

    private final int code = 10002;
    private final int httpStatusCode = 500;
    private final String message;

    public HttpMessageNotReadableErrorCode(String message) {
        this.message = message;
    }
}
