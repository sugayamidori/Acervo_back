package com.github.petervl80.acervoapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Multa {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusMulta status;
    @Column
    private String linkPagamento;;


    private LocalDate dataGeracao;
    private LocalDate dataPagamento;
    @Column
    private String idempotencyKey;

    private String mercadoPagoPaymentId;
    private String statusPagamento;
    private String qrCodeBase64;  // para pix
    private String qrCodeText;
    private String metodoPagamento;
}
