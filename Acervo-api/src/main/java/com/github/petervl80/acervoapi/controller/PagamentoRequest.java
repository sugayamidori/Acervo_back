package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.model.MetodoPagamento;

import java.math.BigDecimal;

public record PagamentoRequest(
        MetodoPagamento metodo,
        BigDecimal valor,
        String numeroCartao,
        String nomeTitular,
        String validade,
        String cvv
) {}
