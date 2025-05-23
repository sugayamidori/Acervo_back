package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.ClientDTO;
import com.github.petervl80.acervoapi.controller.mappers.ClientMapper;
import com.github.petervl80.acervoapi.model.Client;
import com.github.petervl80.acervoapi.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService service;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientController controller;

    private ClientDTO dto;
    private Client client;

    @BeforeEach
    void setUp() {
        dto = new ClientDTO("client-id", "client-secret", "http://localhost/callback", "read write");
        client = new Client();
        client.setId(UUID.randomUUID());
        client.setClientId(dto.clientId());
        client.setClientSecret(dto.clientSecret());
        client.setRedirectURI(dto.redirectURI());
        client.setScope(dto.scope());
    }

    @Test
    void deveSalvarClientComSucessoERetornarStatusCreated() {
        when(mapper.toEntity(dto)).thenReturn(client);

        ResponseEntity<Void> response = controller.salvar(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains(client.getId().toString()));
        verify(mapper).toEntity(dto);
        verify(service).salvar(client);
    }
}