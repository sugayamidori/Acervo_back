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

    @OneToOne(mappedBy = "emprestimo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Multa multa;

    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;

    @ManyToOne
    private Usuario registradoPor;
}
