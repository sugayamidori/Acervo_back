package com.github.petervl80.acervoapi.controller.dto;

import com.github.petervl80.acervoapi.model.DisponibilidadeEnum;
import com.github.petervl80.acervoapi.model.GeneroLivro;

import java.time.LocalDate;
import java.util.UUID;

public record ResultadoPesquisaLivroDTO(
        UUID id,
        String isbn,
        String titulo,
        LocalDate dataPublicacao,
        GeneroLivro genero,
        String autor,
        String sumario,
        DisponibilidadeEnum status,
        String imagem
) {

}
