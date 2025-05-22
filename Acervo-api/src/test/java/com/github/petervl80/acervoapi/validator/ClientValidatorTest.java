package com.github.petervl80.acervoapi.validator;

import com.github.petervl80.acervoapi.exceptions.RegistroDuplicadoException;
import com.github.petervl80.acervoapi.model.Client;
import com.github.petervl80.acervoapi.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientValidatorTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientValidator validator;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setClientId("client-id");
    }

    @Test
    void deveValidarQuandoClienteNaoExiste() {
        when(repository.findByClientId(client.getClientId())).thenReturn(null);

        assertDoesNotThrow(() -> validator.validar(client));

        verify(repository).findByClientId(client.getClientId());
    }

    @Test
    void deveLancarExcecaoQuandoCadastrarComClientIdCadastradoParaOutroClient() {
        client.setId(null);

        Client clientExistente = new Client();
        clientExistente.setClientId(client.getClientId());
        clientExistente.setId(UUID.randomUUID());

        when(repository.findByClientId(client.getClientId())).thenReturn(clientExistente);

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () -> validator.validar(client));

        verify(repository).findByClientId(client.getClientId());
        assertEquals("Client já cadastrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarClientComClientIdCadastradoParaOutroClient() {
        client.setId(UUID.randomUUID());

        Client clientExistente = new Client();
        clientExistente.setClientId(client.getClientId());
        clientExistente.setId(UUID.randomUUID());

        when(repository.findByClientId(client.getClientId())).thenReturn(clientExistente);

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () -> validator.validar(client));

        verify(repository).findByClientId(client.getClientId());
        assertEquals("Client já cadastrado", ex.getMessage());
    }

    @Test
    void naoDeveLancarExcecaoQuandoClienteExisteComMesmoId() {
        client.setId(UUID.randomUUID());

        Client clientExistente = new Client();
        clientExistente.setClientId(client.getClientId());
        clientExistente.setId(client.getId());

        when(repository.findByClientId(client.getClientId())).thenReturn(clientExistente);

        assertDoesNotThrow(() -> validator.validar(client));

        verify(repository).findByClientId(client.getClientId());
    }

}