package com.github.petervl80.acervoapi.controller.common;

import com.github.petervl80.acervoapi.controller.dto.CadastroLivroDTO;
import com.github.petervl80.acervoapi.controller.dto.ErroResposta;
import com.github.petervl80.acervoapi.exceptions.*;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleMethodArgumentNotValidExceptionComStatus422() throws NoSuchMethodException {
        class Dummy {
            public void dummyMethod(@Valid CadastroLivroDTO dto) {}
        }

        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = List.of(
                new FieldError("dto", "titulo", "Campo obrigatório."),
                new FieldError("dto", "autor", "Campo obrigatório.")
        );

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        Method method = Dummy.class.getMethod("dummyMethod", CadastroLivroDTO.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErroResposta resposta = handler.handleMethodArgumentNotValidException(ex);

        assertEquals(422, resposta.status());
        assertEquals("Erro de Validação", resposta.mensagem());
        assertEquals(2, resposta.erros().size());

        assertEquals("titulo", resposta.erros().get(0).campo());
        assertEquals("Campo obrigatório.", resposta.erros().get(0).erro());

        assertEquals("autor", resposta.erros().get(1).campo());
        assertEquals("Campo obrigatório.", resposta.erros().get(1).erro());
    }

    @Test
    void handleRegistroDuplicadoExceptionComStatus409() {
        RegistroDuplicadoException ex = new RegistroDuplicadoException("Livro já está cadastrado.");

        ErroResposta resposta = handler.handleRegistroDuplicadoException(ex);

        assertEquals(409, resposta.status());
        assertEquals("Livro já está cadastrado.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

    @Test
    void handleOperecaoNaoPermitidaExceptionComStatus400() {
        OperecaoNaoPermitidaException ex = new OperecaoNaoPermitidaException("Operação não permitida.");

        ErroResposta resposta = handler.handleOperecaoNaoPermitidaException(ex);

        assertEquals(400, resposta.status());
        assertEquals("Operação não permitida.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

    @Test
    void handleCampoInvalidoExceptionComStatus422() {
        CampoInvalidoException ex = new CampoInvalidoException("Email", "Campo inválido");

        ErroResposta resposta = handler.handleCampoInvalidoException(ex);

        assertEquals(422, resposta.status());
        assertEquals("Erro de Validação", resposta.mensagem());
        assertEquals(1, resposta.erros().size());

        assertEquals("Email", resposta.erros().getFirst().campo());
        assertEquals("Campo inválido", resposta.erros().getFirst().erro());
    }

    @Test
    void handleAccessDeniedExceptionComStatus403() {
        AccessDeniedException ex = new AccessDeniedException("Acesso negado");

        ErroResposta resposta = handler.handleAccessDeniedException(ex);

        assertEquals(403, resposta.status());
        assertEquals("Acesso Negado.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

    @Test
    void handleErrosNaoTratadosComStatus500() {
        RuntimeException ex = new RuntimeException("Falha inesperada");

        ErroResposta resposta = handler.handleErrosNaoTratados(ex);

        assertEquals(500, resposta.status());
        assertEquals("Ocorreu um erro inesperado. Entre em contato com a administração.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

    @Test
    void handleUsuarioNaoEncontradoExceptionComStatus404() {
        UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException("Usuário não encontrado.");

        ErroResposta resposta = handler.handleUsuarioNaoEncontradoException(ex);

        assertEquals(404, resposta.status());
        assertEquals("Usuário não encontrado.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

    @Test
    void handleLivroNaoEncontradoExceptionComStatus404() {
        LivroNaoEncontradoException ex = new LivroNaoEncontradoException("Livro não encontrado.");

        ErroResposta resposta = handler.handleLivroNaoEncontradoException(ex);

        assertEquals(404, resposta.status());
        assertEquals("Livro não encontrado.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }
}