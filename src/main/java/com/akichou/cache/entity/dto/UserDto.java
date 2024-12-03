package com.akichou.cache.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotBlank String username,
        @NotNull Integer age,
        @NotNull Boolean isVip) {
}
