package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/{idEmprestimo}")
    public Multa processarPagamento(@PathVariable UUID idEmprestimo,
                                    @RequestBody PagamentoRequestDTO request) {
        System.out.println("Processando pagamento de multa do empréstimo " + idEmprestimo);
        System.out.println("Método de Pagamento: " + request.getMetodoPagamento() + ", Status: " + request.getSucesso());

        return pagamentoService.processamentoPagamentoMulta(idEmprestimo, request);
    }
}