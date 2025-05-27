package com.github.petervl80.acervoapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUsuarioDTO(
        @NotBlank(message = "Campo obrigatório")
        String email,
        @NotBlank(message = "Campo obrigatório")
        String senha){
}
