package com.github.petervl80.acervoapi.controller.dto;

public record ClientDTO(
        String clientId,
        String clientSecret,
        String redirectURI,
        String scope
) {
}
