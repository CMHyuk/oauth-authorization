package com.oauth.authorization.global.exception;

import lombok.Getter;

@Getter
public class BindErrorCode implements ErrorCode {

    private final int code = 10003;
    private final int httpStatusCode = 400;
    private final String message;

    public BindErrorCode(String message) {
        this.message = message;
    }
}
