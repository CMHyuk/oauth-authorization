package com.oauth.authorization.domain.tenant.exception;

import com.oauth.authorization.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum TenantErrorCode implements ErrorCode {

    NOT_FOUND(1001, 404, "존재하지 않은 테넌트입니다."),
    INVALID_MASTER_TENANT_NAME_REQUEST(1002, 400, "잘못된 마스터 테넌트 이름 요청입니다.");

    private final int code;
    private final int httpStatusCode;
    private final String message;
}
