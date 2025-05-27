package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.DocumentoIdentificacaoDTO;
import com.github.petervl80.acervoapi.controller.dto.PagadorDTO;
import com.github.petervl80.acervoapi.controller.dto.PagamentoRequestDTO;
import com.github.petervl80.acervoapi.model.*;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.MultaRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @InjectMocks
    private PagamentoService pagamentoService;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private PaymentClient paymentClient;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field tokenField = PagamentoService.class.getDeclaredField("mercadoPagoToken");
        tokenField.setAccessible(true);
        tokenField.set(pagamentoService, "test_token");
    }

    @Test
    void realizarPagamentoComMercadoPago_deveRealizarPagamentoComSucesso() throws Exception {
        UUID emprestimoId = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        Multa multa = new Multa();
        multa.setValor(new BigDecimal("10.00"));

        emprestimo.setMulta(multa);

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));

        // Mock para Payment usando RETURNS_DEEP_STUBS para lidar com métodos aninhados
        // Não precisamos mockar PaymentPointOfInteraction ou PaymentTransactionData explicitamente
        Payment payment = mock(Payment.class, RETURNS_DEEP_STUBS);

        when(payment.getId()).thenReturn(123456L);
        when(payment.getPaymentMethodId()).thenReturn("pix");

        // Configura os dados específicos do PIX através do mock "profundo"
        when(payment.getPointOfInteraction().getTransactionData().getQrCodeBase64()).thenReturn("base64_qr_code");
        when(payment.getPointOfInteraction().getTransactionData().getQrCode()).thenReturn("text_qr_code");
        when(payment.getPointOfInteraction().getTransactionData().getTicketUrl()).thenReturn("http://ticket.url/123");


        when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class))).thenReturn(payment);

        DocumentoIdentificacaoDTO doc = new DocumentoIdentificacaoDTO("CPF", "12345678900");
        PagadorDTO pagador = new PagadorDTO("User", "Test", "user@teste.com", doc);
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO(
                new BigDecimal("10.00"),
                "token-teste",
                "pix",
                1,
                "Multa Teste",
                pagador
        );

        Multa resultado = pagamentoService.realizarPagamentoComMercadoPago(emprestimoId, requestDTO);

        assertThat(resultado.getIdempotencyKey()).isNotNull();
        assertThat(resultado.getMercadoPagoPaymentId()).isEqualTo("123456");
        assertThat(resultado.getMetodoPagamento()).isEqualTo("pix");
        assertThat(resultado.getStatusPagamento()).isEqualTo("pending");
        assertThat(resultado.getStatus()).isEqualTo(StatusMulta.PENDENTE);

        // Assertions adicionais para os dados do PIX
        assertThat(resultado.getQrCodeBase64()).isEqualTo("base64_qr_code");
        assertThat(resultado.getQrCodeText()).isEqualTo("text_qr_code");
        assertThat(resultado.getLinkPagamento()).isEqualTo("http://ticket.url/123");


        // Salva duas vezes: idempotencyKey e dados finais
        verify(multaRepository, times(2)).save(multa);
    }

    @Test
    void realizarPagamentoComMercadoPago_quandoNaoTemMulta_deveLancarExcecao() {
        UUID emprestimoId = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setMulta(null);

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));

        DocumentoIdentificacaoDTO doc = new DocumentoIdentificacaoDTO("CPF", "12345678900");
        PagadorDTO pagador = new PagadorDTO("User", "Test", "user@teste.com", doc);
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO(
                new BigDecimal("10.00"),
                "token-teste",
                "pix",
                1,
                "Multa Teste",
                pagador
        );

        assertThatThrownBy(() -> pagamentoService.realizarPagamentoComMercadoPago(emprestimoId, requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Este empréstimo não possui multa pendente.");
    }

    @Test
    void realizarPagamentoComMercadoPago_quandoEmprestimoNaoEncontrado_deveLancarExcecao() {
        UUID emprestimoId = UUID.randomUUID();

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.empty());

        DocumentoIdentificacaoDTO doc = new DocumentoIdentificacaoDTO("CPF", "12345678900");
        PagadorDTO pagador = new PagadorDTO("User", "Test", "user@teste.com", doc);
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO(
                new BigDecimal("10.00"),
                "token-teste",
                "pix",
                1,
                "Multa Teste",
                pagador
        );

        assertThatThrownBy(() -> pagamentoService.realizarPagamentoComMercadoPago(emprestimoId, requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Empréstimo não encontrado");
    }

    @Test
    void realizarPagamentoComMercadoPago_quandoErroMercadoPago_deveLancarExcecao() throws Exception {
        UUID emprestimoId = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        Multa multa = new Multa();
        multa.setValor(new BigDecimal("10.00"));
        emprestimo.setMulta(multa);

        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));

        MPApiException exception = mock(MPApiException.class);
        when(exception.getApiResponse()).thenReturn(mock(com.mercadopago.net.MPResponse.class));
        when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class))).thenThrow(exception);

        DocumentoIdentificacaoDTO doc = new DocumentoIdentificacaoDTO("CPF", "12345678900");
        PagadorDTO pagador = new PagadorDTO("User", "Test", "user@teste.com", doc);
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO(
                new BigDecimal("10.00"),
                "token-teste",
                "pix",
                1,
                "Multa Teste",
                pagador
        );

        assertThatThrownBy(() -> pagamentoService.realizarPagamentoComMercadoPago(emprestimoId, requestDTO))
                .isEqualTo(exception);

        // A primeira chamada ao save (para idempotencyKey) deve ocorrer
        verify(multaRepository, times(1)).save(multa);
    }
}