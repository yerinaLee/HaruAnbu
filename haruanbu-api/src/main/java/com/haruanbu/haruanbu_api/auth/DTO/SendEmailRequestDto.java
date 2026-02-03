package com.haruanbu.haruanbu_api.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailRequestDto(
        @NotBlank @Email String email
) {}