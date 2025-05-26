package com.github.petervl80.acervoapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioMembroDTO(
        @NotBlank(message = "Campo obrigatório")
        String nome,
        @NotBlank(message = "Campo obrigatório")
        String senha,
        @Email (message = "inválido")
        @NotBlank(message = "Campo obrigatório")
        String email) {
}
