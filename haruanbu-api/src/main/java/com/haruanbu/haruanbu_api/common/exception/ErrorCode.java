package com.haruanbu.haruanbu_api.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    MISSING_USER_HEADER(HttpStatus.BAD_REQUEST),
    INVALID_USER_HEADER(HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    FORBIDDEN(HttpStatus.FORBIDDEN),

    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND),

    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVITE_EXPIRED(HttpStatus.BAD_REQUEST),
    INVITE_ALREADY_USED(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {this.status = status;}

    public HttpStatus status() { return status; }

}
