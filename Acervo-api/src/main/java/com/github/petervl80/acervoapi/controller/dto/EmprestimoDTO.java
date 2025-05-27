package com.github.petervl80.acervoapi.controller.dto;

import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.model.StatusEmprestimo;
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
        Multa multa,
        LocalDate dataPagamentoMulta,
        StatusEmprestimo status
) {}
