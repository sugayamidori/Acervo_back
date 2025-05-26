package com.github.petervl80.acervoapi.controller.dto;

import java.util.UUID;

public record LoginResponse(
        UUID usuario,
        String access_token,
        String scope,
        String token_type,
        int expires_in) {
}
