package com.github.petervl80.acervoapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class EmprestimoListagemDTO {
    private UUID id;
    private String livroTitulo;
    private String usuarioNome;
    private LocalDate dataDevolucao;
    private Long diasAtraso;
    private BigDecimal valorMulta;
}
