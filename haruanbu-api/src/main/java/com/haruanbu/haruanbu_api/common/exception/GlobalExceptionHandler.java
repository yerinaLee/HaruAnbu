package com.haruanbu.haruanbu_api.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handle(ApiException e){
        return ResponseEntity
                .status(e.getCode().status())
                .body(ApiErrorResponse.of(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception e){
        return ResponseEntity
                .internalServerError()
                .body(ApiErrorResponse.of(nullSafe("INTERNAL_ERROR"), e.getMessage()));
    }

    private ErrorCode nullSafe(String name){
        return ErrorCode.FORBIDDEN;
    }


}
