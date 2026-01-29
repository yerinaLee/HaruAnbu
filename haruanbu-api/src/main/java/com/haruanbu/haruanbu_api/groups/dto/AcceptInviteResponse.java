package com.haruanbu.haruanbu_api.groups.dto;

import java.util.UUID;

public record AcceptInviteResponse(
        UUID groupId,
        String joinedAs
) {}
