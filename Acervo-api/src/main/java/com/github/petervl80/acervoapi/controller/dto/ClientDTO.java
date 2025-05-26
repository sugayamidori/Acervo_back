package com.github.petervl80.acervoapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientDTO(
        @NotBlank(message = "Campo obrigatório")
        String clientId,
        @NotBlank(message = "Campo obrigatório")
        String clientSecret,
        @NotBlank(message = "Campo obrigatório")
        String redirectURI,
        @NotBlank(message = "Campo obrigatório")
        String scope
) {
}
