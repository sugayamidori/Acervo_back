package com.github.petervl80.acervoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "emprestimo")
@Getter
@Setter
public class Emprestimo {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "membro_id")
    private Usuario membro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id")
    private Livro livro;

    @Column
    private LocalDate dataEmprestimo;

    @Column
    private LocalDate dataLimiteDevolucao;

    @Column
    private LocalDate dataDevolucao;

    @OneToOne(mappedBy = "emprestimo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Multa multa;

    @Enumerated(EnumType.STRING)
    @Column
    private StatusEmprestimo status;

    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;
}
