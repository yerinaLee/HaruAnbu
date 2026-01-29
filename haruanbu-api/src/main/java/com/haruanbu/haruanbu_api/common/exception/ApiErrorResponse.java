package com.haruanbu.haruanbu_api.common.exception;

import java.time.OffsetDateTime;

public record ApiErrorResponse (
    String errorCode,
    String message,
    OffsetDateTime timestamp
) {
    public static ApiErrorResponse of(ErrorCode code, String message){
        return new ApiErrorResponse(code.name(), message, OffsetDateTime.now());
    }
}