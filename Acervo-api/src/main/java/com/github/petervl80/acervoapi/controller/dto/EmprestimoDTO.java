package com.github.petervl80.acervoapi.controller.dto;

import com.github.petervl80.acervoapi.model.StatusEmprestimo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoDTO(
        UUID id,
        UUID membroId,
        UUID livroId,
        String livroTitulo,
        LocalDate dataEmprestimo,
        LocalDate dataLimiteDevolucao,
        LocalDate dataDevolucao,
        BigDecimal multa,
        LocalDate dataPagamentoMulta,
        StatusEmprestimo status
) {}
