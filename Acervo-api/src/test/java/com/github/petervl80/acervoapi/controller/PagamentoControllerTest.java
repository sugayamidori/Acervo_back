package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.DocumentoIdentificacaoDTO;
import com.github.petervl80.acervoapi.controller.dto.PagadorDTO;
import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoControllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController controller;

    private UUID idEmprestimo;
    private PagamentoRequestDTO requestDTO;
    private Multa multa;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        idEmprestimo = UUID.randomUUID();
        DocumentoIdentificacaoDTO doc = new DocumentoIdentificacaoDTO("CPF", "12345678900");
        PagadorDTO pagador = new PagadorDTO("User", "Test", "user@teste.com", doc);
        requestDTO = new PagamentoRequestDTO(
                new BigDecimal("10.00"),
                "token-teste",
                "pix",
                1,
                "Pagamento Teste",
                pagador
        );
        multa = new Multa();
    }

    @Test
    void deveProcessarPagamentoComSucesso() throws Exception {
        when(pagamentoService.realizarPagamentoComMercadoPago(idEmprestimo, requestDTO)).thenReturn(multa);

        Multa response = controller.processarPagamento(idEmprestimo, requestDTO);

        assertNotNull(response);
        assertEquals(multa, response);
        verify(pagamentoService).realizarPagamentoComMercadoPago(idEmprestimo, requestDTO);
    }
}
