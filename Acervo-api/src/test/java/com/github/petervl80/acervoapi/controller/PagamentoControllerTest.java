package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.exceptions.ResourceNotFoundException;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.model.StatusMulta;
import com.github.petervl80.acervoapi.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoControllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    private UUID idEmprestimo;
    private PagamentoRequestDTO requestDTO;
    private Multa multaSimulada;

    @BeforeEach
    void setUp() {
        idEmprestimo = UUID.randomUUID();
        requestDTO = new PagamentoRequestDTO("CARTAO", true);

        // Crie uma Multa simulada que o service retornaria
        multaSimulada = Multa.builder()
                .id(UUID.randomUUID())
                .valor(BigDecimal.valueOf(10.00))
                .status(StatusMulta.PAGA)
                .mercadoPagoPaymentId(UUID.randomUUID().toString())
                .statusPagamento("approved")
                .metodoPagamento("CARTAO")
                .dataPagamento(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Deve chamar o serviço e retornar a multa processada com sucesso")
    void deveProcessarPagamentoComSucesso() throws Exception {

        when(pagamentoService.processamentoPagamentoMulta(eq(idEmprestimo), any(PagamentoRequestDTO.class)))
                .thenReturn(multaSimulada);


        Multa result = pagamentoController.processarPagamento(idEmprestimo, requestDTO);


        assertNotNull(result);
        assertEquals(multaSimulada.getId(), result.getId());
        assertEquals(multaSimulada.getStatus(), result.getStatus());
        assertEquals(multaSimulada.getStatusPagamento(), result.getStatusPagamento());
        assertEquals(multaSimulada.getMetodoPagamento(), result.getMetodoPagamento());

        verify(pagamentoService, times(1)).processamentoPagamentoMulta(eq(idEmprestimo), any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve propagar ResourceNotFoundException do serviço")
    void devePropagarResourceNotFoundException() {

        when(pagamentoService.processamentoPagamentoMulta(eq(idEmprestimo), any(PagamentoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Empréstimo não encontrado para pagamento."));


        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                pagamentoController.processarPagamento(idEmprestimo, requestDTO));

        assertEquals("Empréstimo não encontrado para pagamento.", thrown.getMessage());

        verify(pagamentoService, times(1)).processamentoPagamentoMulta(eq(idEmprestimo), any(PagamentoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve propagar IllegalStateException do serviço")
    void devePropagarIllegalStateException() {
        when(pagamentoService.processamentoPagamentoMulta(eq(idEmprestimo), any(PagamentoRequestDTO.class)))
                .thenThrow(new IllegalStateException("Multa já processada ou rejeitada."));


        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                pagamentoController.processarPagamento(idEmprestimo, requestDTO));

        assertEquals("Multa já processada ou rejeitada.", thrown.getMessage());

        verify(pagamentoService, times(1)).processamentoPagamentoMulta(eq(idEmprestimo), any(PagamentoRequestDTO.class));
    }

}