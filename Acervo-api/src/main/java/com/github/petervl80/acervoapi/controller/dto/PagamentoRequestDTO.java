package com.github.petervl80.acervoapi.controller.dto;

import java.math.BigDecimal;

public record PagamentoRequestDTO(
        BigDecimal transactionAmount,
        String token, // Para cartão
        String paymentMethodId, // "pix", "bolbradesco", etc.
        Integer installments,
        String description,
        PagadorDTO payer
) {}
