package com.github.petervl80.acervoapi.controller.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErroRespostaTest {

    private String mensagem;

    @Test
    void respostaPadrao() {
        mensagem = "Erro generico.";

        ErroResposta resposta = ErroResposta.respostaPadrao(mensagem);

        assertEquals(400, resposta.status());
        assertEquals("Erro generico.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

    @Test
    void conflito() {
        mensagem = "Erro de conflito.";

        ErroResposta resposta = ErroResposta.conflito(mensagem);

        assertEquals(409, resposta.status());
        assertEquals("Erro de conflito.", resposta.mensagem());
        assertTrue(resposta.erros().isEmpty());
    }

}