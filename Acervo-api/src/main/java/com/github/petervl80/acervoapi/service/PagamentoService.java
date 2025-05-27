package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.exceptions.OperecaoNaoPermitidaException;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.model.StatusMulta;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.MultaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class PagamentoService {

    private final EmprestimoRepository emprestimoRepository;
    private final MultaRepository multaRepository;
    @Value("${mercado.pago.token}")
    private String mercadoPagoToken;

    @PreAuthorize("hasRole('MEMBRO')")
    public Multa realizarPagamentoComMercadoPago(UUID idEmprestimo, PagamentoRequestDTO request) throws Exception {

        MercadoPagoConfig.setAccessToken(mercadoPagoToken);

        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        Multa multa = emprestimo.getMulta();
        if (multa == null || multa.getValor() == null) {
            throw new IllegalArgumentException("Este empréstimo não possui multa pendente.");
        }

        if (multa.getIdempotencyKey() == null) {
            multa.setIdempotencyKey(UUID.randomUUID().toString());
            multaRepository.save(multa);
        }

        PaymentClient client = new PaymentClient();

        PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                .transactionAmount(request.transactionAmount())
                .description(request.description())
                .paymentMethodId(request.paymentMethodId())
                .dateOfExpiration(OffsetDateTime.now().plusDays(3))
                .installments(request.installments())
                .token(request.token())
                .payer(
                        PaymentPayerRequest.builder()
                                .email(request.payer().login())
                                .firstName(
                                        request.payer().firstName() != null && !request.payer().firstName().isBlank()
                                                ? request.payer().firstName()
                                                : request.payer().login()
                                )
                                .lastName(
                                        request.payer().lastName() != null && !request.payer().lastName().isBlank()
                                                ? request.payer().lastName()
                                                : "Usuario"
                                )
                                .identification(
                                        IdentificationRequest.builder()
                                                .type(request.payer().identification().type())
                                                .number(request.payer().identification().number())
                                                .build())
                                .build()
                )
                .build();

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", multa.getIdempotencyKey());

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build();

        try {
            Payment payment = client.create(paymentCreateRequest, requestOptions);

            multa.setMercadoPagoPaymentId(payment.getId().toString());
            multa.setMetodoPagamento(payment.getPaymentMethodId());

            multa.setStatusPagamento("pending");
            multa.setStatus(StatusMulta.PENDENTE);

            if ("pix".equals(payment.getPaymentMethodId())) {
                multa.setQrCodeBase64(payment.getPointOfInteraction().getTransactionData().getQrCodeBase64());
                multa.setQrCodeText(payment.getPointOfInteraction().getTransactionData().getQrCode());
                multa.setLinkPagamento(payment.getPointOfInteraction().getTransactionData().getTicketUrl());
            }

            multaRepository.save(multa);
            return multa;

        } catch (MPApiException e) {
            System.out.println("Erro do Mercado Pago: " + e.getApiResponse().getContent());
            throw e;
        }
    }
}
