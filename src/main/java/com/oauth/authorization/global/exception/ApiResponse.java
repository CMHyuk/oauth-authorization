package com.oauth.authorization.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T response;
    private Integer code;
    private String errorMessage;

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public ApiResponse(boolean success, T response) {
        this.success = success;
        this.response = response;
    }

    public ApiResponse(boolean success, Integer code, String errorMessage) {
        this.success = success;
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public static <T> ApiResponse success(T response) {
        return new ApiResponse<>(true, response);
    }

    public static ApiResponse success() {
        return new ApiResponse(true);
    }

    public static ApiResponse error(ErrorCode errorCode) {
        return new ApiResponse(false, errorCode.getCode(), errorCode.getMessage());
    }
}
