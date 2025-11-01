package com.sistema_hospitalar.dto;

import com.sistema_hospitalar.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull UserRole role
        ) {}
