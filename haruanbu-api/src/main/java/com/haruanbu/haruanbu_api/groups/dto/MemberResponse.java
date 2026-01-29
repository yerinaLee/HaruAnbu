package com.haruanbu.haruanbu_api.groups.dto;

import java.util.UUID;

public record MemberResponse(
        UUID userId,
        String role
) {}
