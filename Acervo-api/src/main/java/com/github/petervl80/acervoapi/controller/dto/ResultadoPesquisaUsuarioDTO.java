package com.github.petervl80.acervoapi.controller.dto;

import java.util.List;

public record ResultadoPesquisaUsuarioDTO(
        String login,
        String email,
        List<String> roles) {
}
