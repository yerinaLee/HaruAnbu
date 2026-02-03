package com.haruanbu.haruanbu_api.auth.DTO;

public record SendEmailResponseDto(
        boolean sent,
        int cooldownSeconds,
        int expiresInSeconds
) {}
