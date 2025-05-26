package com.github.petervl80.acervoapi.validator;

import com.github.petervl80.acervoapi.exceptions.RegistroDuplicadoException;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivroValidatorTest {

    @Mock
    private LivroRepository repository;

    @InjectMocks
    private LivroValidator validator;

    private Livro livro;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setId(null);
        livro.setIsbn("1234567890");
    }

    @Test
    void devePermitirCadastroDoLivroQuandoIsbnNaoExiste() {
        when(repository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validar(livro));
    }

    @Test
    void deveLancarExcecaoQuandoIsbnJaExisteParaNovoLivro() {
        Livro duplicado = new Livro();
        duplicado.setId(UUID.randomUUID());
        duplicado.setIsbn("1234567890");

        when(repository.findByIsbn("1234567890")).thenReturn(Optional.of(duplicado));

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () ->
            validator.validar(livro)
        );

        assertEquals("ISBN já cadastrado para outro livro", ex.getMessage());
    }

    @Test
    void devePermitirAtualizacaoQuandoMesmoId() {
        UUID id = UUID.randomUUID();
        livro.setId(id);

        Livro existente = new Livro();
        existente.setId(id);
        existente.setIsbn("1234567890");

        when(repository.findByIsbn("1234567890")).thenReturn(Optional.of(existente));

        assertDoesNotThrow(() -> validator.validar(livro));
    }

    @Test
    void deveLancarExcecaoQuandoIsbnExisteComOutroId() {
        livro.setId(UUID.randomUUID());

        Livro existente = new Livro();
        existente.setId(UUID.randomUUID());
        existente.setIsbn("1234567890");

        when(repository.findByIsbn("1234567890")).thenReturn(Optional.of(existente));

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () -> validator.validar(livro));

        assertEquals("ISBN já cadastrado para outro livro", ex.getMessage());
    }
}
