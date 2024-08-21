package com.oauth.authorization.domain.user.exception;

import com.oauth.authorization.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND(3001, 404, "존재하지 않는 사용자입니다."),
    PASSWORD_MISMATCH(3002, 401, "비밀번호가 일치하지 않습니다.")
    ;

    private final int code;
    private final int httpStatusCode;
    private final String message;
}
