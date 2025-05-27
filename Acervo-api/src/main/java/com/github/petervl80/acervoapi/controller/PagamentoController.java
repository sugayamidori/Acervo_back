package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/{idEmprestimo}")
    public Multa processarPagamento(@PathVariable UUID idEmprestimo,
                                    @RequestBody PagamentoRequestDTO request) throws Exception {
        return pagamentoService.realizarPagamentoComMercadoPago(idEmprestimo, request);
    }

}
