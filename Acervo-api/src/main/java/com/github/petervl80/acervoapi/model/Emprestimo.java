package com.github.petervl80.acervoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Emprestimo {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Usuario membro;

    @ManyToOne(optional = false)
    private Livro livro;

    private LocalDate dataEmprestimo;
    private LocalDate dataLimiteDevolucao;
    private LocalDate dataDevolucao;

    private BigDecimal multa;
    private LocalDate dataPagamentoMulta;


    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;

    @ManyToOne
    private Usuario registradoPor;
}

