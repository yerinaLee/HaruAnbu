package com.haruanbu.haruanbu_api.groups.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupRequest (
        @NotBlank String name
){}
