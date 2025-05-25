package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoDTO;
import com.github.petervl80.acervoapi.controller.dto.EmprestimoListagemDTO;
import com.github.petervl80.acervoapi.controller.mappers.EmprestimoMapper;
import com.github.petervl80.acervoapi.service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;
    private final EmprestimoMapper mapper;

    @PostMapping
    public ResponseEntity<EmprestimoDTO> registrarEmprestimo(
            @RequestParam("idLivro") UUID idLivro,
            @RequestParam("idMembro") UUID idMembro
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

    @PostMapping("/reserva")
    public ResponseEntity<EmprestimoDTO> reservarLivro(@RequestParam("idLivro") UUID idLivro) {
        return ResponseEntity.ok(mapper.toDTO(service.reservarLivro(idLivro)));
    }

    @PutMapping("/{id}/liberar")
    public ResponseEntity<EmprestimoDTO> liberarEmprestimo(@PathVariable UUID id) {
        return ResponseEntity.ok(
                mapper.toDTO(service.liberarEmprestimo(id))
        );
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoListagemDTO>> listarEmprestimos() {
        return ResponseEntity.ok(service.listarEmprestimos());
    }
}
