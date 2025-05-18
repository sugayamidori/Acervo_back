package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoDTO;
import com.github.petervl80.acervoapi.controller.mappers.EmprestimoMapper;
import com.github.petervl80.acervoapi.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;
    private final EmprestimoMapper mapper;

    @PostMapping
    public ResponseEntity<EmprestimoDTO> registrarEmprestimo(
            @RequestParam UUID idLivro,
            @RequestParam UUID idMembro
    ) {
        return ResponseEntity.ok(
                mapper.toDTO(service.registrarEmprestimo(idLivro, idMembro))
        );
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<EmprestimoDTO> devolverLivro(@PathVariable UUID id) {
        return ResponseEntity.ok(
                mapper.toDTO(service.devolver(id))
        );
    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<EmprestimoDTO> realizarPagamento(
            @PathVariable UUID id,
            @RequestBody PagamentoRequest request
    ) {
        return ResponseEntity.ok(
                mapper.toDTO(service.realizarPagamento(id, request))
        );
    }
    @PostMapping("/reserva")
    public ResponseEntity<EmprestimoDTO> reservarLivro(@RequestParam UUID idLivro) {
        return ResponseEntity.ok(mapper.toDTO(service.reservarLivro(idLivro)));
    }
    @PutMapping("/{id}/liberar")
    public ResponseEntity<EmprestimoDTO> liberarEmprestimo(@PathVariable UUID id) {
        return ResponseEntity.ok(
                mapper.toDTO(service.liberarEmprestimo(id))
        );
    }



}
