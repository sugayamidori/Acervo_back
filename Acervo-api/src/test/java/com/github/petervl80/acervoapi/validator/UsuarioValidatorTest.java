package com.github.petervl80.acervoapi.validator;

import com.github.petervl80.acervoapi.exceptions.RegistroDuplicadoException;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioValidatorTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioValidator validator;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("teste@gmail.com");
    }

    @Test
    void devePermitirCadastroDoUsuarioQuandoIsbnNaoExiste() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(null);

        assertDoesNotThrow(() -> validator.validar(usuario));
        verify(repository).findByEmail(usuario.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstaCadastradoParaOutroUsuario() {
        Usuario duplicado = new Usuario();
        duplicado.setId(UUID.randomUUID());
        duplicado.setEmail("teste@gmail.com");

        when(repository.findByEmail(usuario.getEmail())).thenReturn(duplicado);

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () ->
                validator.validar(usuario)
        );

        verify(repository).findByEmail(usuario.getEmail());
        assertEquals("Usuário já cadastrado", ex.getMessage());
    }

    @Test
    void devePermitirAtualizacaoDoUsuarioQuandoMesmoId() {
        UUID id = UUID.randomUUID();
        usuario.setId(id);

        Usuario existente = new Usuario();
        existente.setId(id);
        existente.setEmail(usuario.getEmail());

        when(repository.findByEmail(usuario.getEmail())).thenReturn(existente);

        assertDoesNotThrow(() -> validator.validar(usuario));
        verify(repository).findByEmail(usuario.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoEmailExisteComOutroId() {
        usuario.setId(UUID.randomUUID());

        Usuario existente = new Usuario();
        existente.setId(UUID.randomUUID());
        existente.setEmail(usuario.getEmail());

        when(repository.findByEmail(usuario.getEmail())).thenReturn(existente);

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () -> validator.validar(usuario));

        verify(repository).findByEmail(usuario.getEmail());
        assertEquals("Usuário já cadastrado", ex.getMessage());
    }
}