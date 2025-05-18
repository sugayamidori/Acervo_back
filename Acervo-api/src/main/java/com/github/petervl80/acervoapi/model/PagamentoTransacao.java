package com.github.petervl80.acervoapi.model;


import com.github.petervl80.acervoapi.model.PagamentoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PagamentoTransacao {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID emprestimoId;

    private BigDecimal valor;

    private LocalDate dataSolicitacao;

    private LocalDate dataResposta;

    @Enumerated(EnumType.STRING)
    private PagamentoStatus status;
}
