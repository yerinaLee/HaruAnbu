package com.haruanbu.haruanbu_api.groups.dto;

import java.time.OffsetDateTime;

public record CreateInviteResponse(
        String code,
        OffsetDateTime expiresAt
) {}
