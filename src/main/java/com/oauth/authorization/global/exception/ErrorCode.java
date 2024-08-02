package com.oauth.authorization.global.exception;

public interface ErrorCode {

    int getCode();

    int getHttpStatusCode();

    String getMessage();
}
