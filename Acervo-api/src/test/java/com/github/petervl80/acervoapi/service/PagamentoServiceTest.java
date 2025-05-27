package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.exceptions.ResourceNotFoundException;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.Multa;
import com.github.petervl80.acervoapi.model.StatusMulta;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.MultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private Random mockRandom;

    @InjectMocks
    private PagamentoService pagamentoService;

    private UUID emprestimoId;
    private Emprestimo emprestimo;
    private Multa multaPendente;

    @BeforeEach
    void setUp() {

        emprestimoId = UUID.randomUUID();

        multaPendente = Multa.builder()
                .id(UUID.randomUUID())
                .valor(BigDecimal.valueOf(10.50))
                .status(StatusMulta.PENDENTE)
                .dataGeracao(LocalDate.now().minusDays(5))
                .build();

        emprestimo = Emprestimo.builder()
                .id(emprestimoId)
                .multa(multaPendente)
                .build();

        multaPendente.setEmprestimo(emprestimo);
    }

    @Test
    @DisplayName("Deve processar pagamento com sucesso (status APROVADO)")
    void deveProcessarPagamentoComSucesso() {
        PagamentoRequestDTO request = new PagamentoRequestDTO("CARTAO", true); // Sucesso
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));
        when(multaRepository.save(any(Multa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Multa result = pagamentoService.processamentoPagamentoMulta(emprestimoId, request);

        assertNotNull(result);
        assertEquals(StatusMulta.PAGA, result.getStatus());
        assertEquals("approved", result.getStatusPagamento());
        assertNotNull(result.getMercadoPagoPaymentId());
        assertNotNull(result.getDataPagamento());
        assertEquals(LocalDate.now(), result.getDataPagamento());
        assertNull(result.getLinkPagamento());
        assertNull(result.getQrCodeBase64());
        assertNull(result.getQrCodeText());
        assertEquals("CARTAO", result.getMetodoPagamento());

        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, times(1)).save(any(Multa.class));
    }

    @Test
    @DisplayName("Deve processar pagamento com falha (status REJEITADA)")
    void deveProcessarPagamentoComFalha() {

        PagamentoRequestDTO request = new PagamentoRequestDTO("BOLETO", false); // Falha
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));
        when(multaRepository.save(any(Multa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Multa result = pagamentoService.processamentoPagamentoMulta(emprestimoId, request);

        assertNotNull(result);
        assertEquals(StatusMulta.REJEITADA, result.getStatus());
        assertEquals("rejected", result.getStatusPagamento());
        assertNotNull(result.getMercadoPagoPaymentId());
        assertNull(result.getDataPagamento());
        assertNull(result.getLinkPagamento());
        assertNull(result.getQrCodeBase64());
        assertNull(result.getQrCodeText());
        assertEquals("BOLETO", result.getMetodoPagamento());

        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, times(1)).save(any(Multa.class));
    }

    @Test
    @DisplayName("Deve processar pagamento PIX pendente (status PENDENTE com QR Code)")
    void deveProcessarPagamentoPixPendente() {

        PagamentoRequestDTO request = new PagamentoRequestDTO("PIX", null); // Simulação aleatória
        when(mockRandom.nextInt(100)).thenReturn(85); // Valor entre 80 e 94 (inclusive)

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));
        when(multaRepository.save(any(Multa.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Multa result = pagamentoService.processamentoPagamentoMulta(emprestimoId, request);

        assertNotNull(result);
        assertEquals(StatusMulta.PENDENTE, result.getStatus());
        assertEquals("pending", result.getStatusPagamento());
        assertNotNull(result.getMercadoPagoPaymentId());
        assertNull(result.getDataPagamento());
        assertNotNull(result.getLinkPagamento());
        assertNotNull(result.getQrCodeBase64());
        assertNotNull(result.getQrCodeText());
        assertEquals("PIX", result.getMetodoPagamento());

        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, times(1)).save(any(Multa.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se Empréstimo não for encontrado")
    void deveLancarResourceNotFoundExceptionSeEmprestimoNaoEncontrado() {
        PagamentoRequestDTO request = new PagamentoRequestDTO("CARTAO", true);
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                pagamentoService.processamentoPagamentoMulta(emprestimoId, request));

        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, never()).save(any(Multa.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se Multa não for encontrada para o Empréstimo")
    void deveLancarResourceNotFoundExceptionSeMultaNaoEncontrada() {
        PagamentoRequestDTO request = new PagamentoRequestDTO("CARTAO", true);
        Emprestimo emprestimoSemMulta = Emprestimo.builder().id(emprestimoId).multa(null).build();
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimoSemMulta));

        assertThrows(ResourceNotFoundException.class, () ->
                pagamentoService.processamentoPagamentoMulta(emprestimoId, request));

        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, never()).save(any(Multa.class));
    }

    @Test
    @DisplayName("Não deve processar se Multa já estiver PAGA")
    void naoDeveProcessarSeMultaJaPaga() {
        PagamentoRequestDTO request = new PagamentoRequestDTO("CARTAO", true);
        multaPendente.setStatus(StatusMulta.PAGA); // Mudar status para PAGA
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                pagamentoService.processamentoPagamentoMulta(emprestimoId, request));

        assertEquals("Multa já paga e não pode ser reprocessada.", exception.getMessage()); // Mensagem atualizada
        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, never()).save(any(Multa.class));
    }

    @Test
    @DisplayName("Deve processar multa REJEITADA e mudar para status APROVADO")
    void deveProcessarMultaRejeitadaEStatusAprovado() {
        multaPendente.setStatus(StatusMulta.REJEITADA);
        multaPendente.setStatusPagamento("rejected");
        PagamentoRequestDTO request = new PagamentoRequestDTO("CARTAO", true); // Simular sucesso

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));
        when(multaRepository.save(any(Multa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Multa result = pagamentoService.processamentoPagamentoMulta(emprestimoId, request);

        assertNotNull(result);
        assertEquals(StatusMulta.PAGA, result.getStatus());
        assertEquals("approved", result.getStatusPagamento());
        assertNotNull(result.getMercadoPagoPaymentId());

        verify(emprestimoRepository, times(1)).findById(emprestimoId);
        verify(multaRepository, times(1)).save(any(Multa.class));
    }
}