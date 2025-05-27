package com.github.petervl80.acervoapi.controller.dto;

import java.util.List;
import java.util.UUID;

public record ResultadoPesquisaUsuarioDTO(
        UUID id,
        String nome,
        String email,
        List<String> roles) {
}
