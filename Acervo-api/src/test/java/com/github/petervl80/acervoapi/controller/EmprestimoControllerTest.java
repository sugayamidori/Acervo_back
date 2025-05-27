package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoDTO;
import com.github.petervl80.acervoapi.controller.dto.EmprestimoListagemDTO;
import com.github.petervl80.acervoapi.controller.mappers.EmprestimoMapper;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.StatusEmprestimo;
import com.github.petervl80.acervoapi.service.EmprestimoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmprestimoControllerTest {

    @Mock
    private EmprestimoService service;

    @Mock
    private EmprestimoMapper mapper;

    @InjectMocks
    private EmprestimoController controller;

    private Emprestimo emprestimo;
    private EmprestimoDTO emprestimoDTO;
    private UUID idEmprestimo;
    private UUID idLivro;
    private UUID idMembro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        idEmprestimo = UUID.randomUUID();
        idLivro = UUID.randomUUID();
        idMembro = UUID.randomUUID();

        emprestimo = new Emprestimo();
        EmprestimoDTO emprestimoDTO = new EmprestimoDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Livro A",
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                LocalDate.now(),
                StatusEmprestimo.RESERVADO
        );

    }

    @Test
    void deveDevolverLivroComSucesso() {
        when(service.devolver(idEmprestimo)).thenReturn(emprestimo);
        when(mapper.toDTO(emprestimo)).thenReturn(emprestimoDTO);

        ResponseEntity<EmprestimoDTO> response = controller.devolverLivro(idEmprestimo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emprestimoDTO, response.getBody());
        verify(service).devolver(idEmprestimo);
    }

    @Test
    void deveLiberarEmprestimoComSucesso() {
        when(service.liberarEmprestimo(idEmprestimo)).thenReturn(emprestimo);
        when(mapper.toDTO(emprestimo)).thenReturn(emprestimoDTO);

        ResponseEntity<EmprestimoDTO> response = controller.liberarEmprestimo(idEmprestimo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emprestimoDTO, response.getBody());
        verify(service).liberarEmprestimo(idEmprestimo);
    }


    @Test
    void deveListarEmprestimosComSucesso() {
        EmprestimoListagemDTO listagemDTO = new EmprestimoListagemDTO(
                UUID.randomUUID(),       // id
                "Livro A",               // livroTitulo
                "Usuário A",             // usuarioNome
                LocalDate.now(),         // dataDevolucao
                2L,                      // diasAtraso
                BigDecimal.TEN           // valorMulta
        );

        when(service.listarEmprestimos()).thenReturn(List.of(listagemDTO));

        ResponseEntity<List<EmprestimoListagemDTO>> response = controller.listarEmprestimos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(listagemDTO, response.getBody().getFirst());
        verify(service).listarEmprestimos();
    }

}
