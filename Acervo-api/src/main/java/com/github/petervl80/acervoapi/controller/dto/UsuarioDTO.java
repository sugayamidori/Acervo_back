package com.github.petervl80.acervoapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UsuarioDTO(
        @NotBlank(message = "Campo obrigatório")
        String nome,
        @NotBlank(message = "Campo obrigatório")
        String senha,
        @Email (message = "inválido")
        @NotBlank(message = "Campo obrigatório")
        String email,
        @NotEmpty(message = "Campo obrigatório")
        List<String> roles) {
}
