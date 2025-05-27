package com.github.petervl80.acervoapi.controller.dto;

public record PagadorDTO(
        String firstName,
        String lastName,
        String login,
        DocumentoIdentificacaoDTO identification
) {}
