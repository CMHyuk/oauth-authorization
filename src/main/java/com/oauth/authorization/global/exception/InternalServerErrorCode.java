package com.oauth.authorization.global.exception;

import lombok.Getter;

@Getter
public class InternalServerErrorCode implements ErrorCode {

    private final int code = 10001;
    private final int httpStatusCode = 500;
    private final String message;

    public InternalServerErrorCode(String message) {
        this.message = message;
    }
}
