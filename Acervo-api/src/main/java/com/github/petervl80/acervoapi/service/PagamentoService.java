package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.exceptions.ResourceNotFoundException;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.model.StatusMulta;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final EmprestimoRepository emprestimoRepository;
    private final MultaRepository multaRepository;

    private final Random random = new Random();

    @Transactional
    public Multa processamentoPagamentoMulta(UUID idEmprestimo, PagamentoRequestDTO request) {

        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com o ID: " + idEmprestimo));

        Multa multa = emprestimo.getMulta();

        if (multa == null) {
            throw new ResourceNotFoundException("Multa não encontrada ou não gerada para o empréstimo com ID: " + idEmprestimo);
        }

        if (multa.getStatus() == StatusMulta.PAGA) {
            throw new IllegalStateException("Multa já paga e não pode ser reprocessada.");
        }


        String simulatedPaymentId = UUID.randomUUID().toString();
        String simulatedStatusPagamentoGateway;
        StatusMulta newMultaStatus;

        if (request.getSucesso() != null) {

            if (request.getSucesso()) {
                simulatedStatusPagamentoGateway = "approved";
            } else {
                simulatedStatusPagamentoGateway = "rejected";
            }
        } else {
            int chance = random.nextInt(100);
            if (chance < 80) {
                simulatedStatusPagamentoGateway = "approved";
            } else if (chance < 95) {
                simulatedStatusPagamentoGateway = "pending";
            } else {
                simulatedStatusPagamentoGateway = "rejected";
            }
        }


        multa.setMercadoPagoPaymentId(simulatedPaymentId);
        multa.setMetodoPagamento(request.getMetodoPagamento());
        multa.setIdempotencyKey(UUID.randomUUID().toString());

        switch (simulatedStatusPagamentoGateway) {
            case "approved":
                multa.setStatusPagamento("approved");
                multa.setDataPagamento(LocalDate.now());
                newMultaStatus = StatusMulta.PAGA;
                multa.setLinkPagamento(null);
                multa.setQrCodeBase64(null);
                multa.setQrCodeText(null);
                break;
            case "pending":
                multa.setStatusPagamento("pending");
                newMultaStatus = StatusMulta.PENDENTE;
                if ("PIX".equalsIgnoreCase(request.getMetodoPagamento())) {
                    multa.setQrCodeBase64("simulated_qr_code_base64_for_" + simulatedPaymentId);
                    multa.setQrCodeText("simulated_qr_code_text_for_" + simulatedPaymentId);
                    multa.setLinkPagamento("http://simulated.payment.link/pix/" + simulatedPaymentId);
                }
                break;
            case "rejected":
                multa.setStatusPagamento("rejected");
                newMultaStatus = StatusMulta.REJEITADA;
                multa.setLinkPagamento(null);
                multa.setQrCodeBase64(null);
                multa.setQrCodeText(null);
                break;
            default:

                newMultaStatus = StatusMulta.PENDENTE;
                multa.setStatusPagamento("unknown");
                break;
        }

        multa.setStatus(newMultaStatus);

        return multaRepository.save(multa);
    }
}