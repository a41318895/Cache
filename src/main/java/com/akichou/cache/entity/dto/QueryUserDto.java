package com.akichou.cache.entity.dto;

import jakarta.validation.constraints.NotBlank;

public record QueryUserDto(@NotBlank String id) {
}
