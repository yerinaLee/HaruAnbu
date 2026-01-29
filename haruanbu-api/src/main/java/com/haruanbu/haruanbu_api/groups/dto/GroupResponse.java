package com.haruanbu.haruanbu_api.groups.dto;

import java.util.UUID;

public record GroupResponse(
        UUID groupId,
        String name
) {}
