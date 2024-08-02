package com.oauth.authorization.global.exception.controller;

import com.oauth.authorization.global.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.error("BusinessException : {} ", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleUnExpectedException(Exception exception) {
        InternalServerErrorCode errorCode = new InternalServerErrorCode(exception.getMessage());
        log.error("InternalServerErrorCode : {} ", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        HttpMessageNotReadableErrorCode errorCode = new HttpMessageNotReadableErrorCode(exception.getMessage());
        log.error("HandleHttpMessageNotReadableException : {}", exception.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleBindException(MethodArgumentNotValidException exception) {
        List<String> defaultMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        String message = String.join(", ", defaultMessages);
        BindErrorCode errorCode = new BindErrorCode(message);
        log.error("MethodArgumentNotValidException : {} ", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(ApiResponse.error(errorCode));
    }
}
