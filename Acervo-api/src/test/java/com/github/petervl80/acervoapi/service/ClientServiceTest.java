package com.github.petervl80.acervoapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.petervl80.acervoapi.controller.dto.OAuthTokenResponse;
import com.github.petervl80.acervoapi.model.Client;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.ClientRepository;
import com.github.petervl80.acervoapi.validator.ClientValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private ClientValidator validator;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClientService service;

    private Client client;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(UUID.randomUUID());
        client.setClientId("client-id");
        client.setClientSecret("senha");
        client.setScope("client-scope");
        client.setRedirectURI("http://localhost/oauth2/callback");

        usuario = new Usuario();
        usuario.setNome("user");
        usuario.setSenha("user-password");
        usuario.setEmail("user@user.com");
        usuario.setRoles(List.of("USER"));
    }

    @Test
    void deveSalvarClientComSenhaCriptografada() {
        String senha = client.getClientSecret();
        String senhaCriptografada = "senha-criptografada";

        when(encoder.encode(senha)).thenReturn(senhaCriptografada);
        when(repository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client salvo = service.salvar(client);

        verify(validator).validar(client);
        verify(encoder).encode(senha);
        verify(repository).save(client);

        assertEquals(senhaCriptografada, salvo.getClientSecret());
    }

    @Test
    void deveObterClientPorClientId() {
        when(repository.findByClientId(client.getClientId())).thenReturn(client);

        Client resultado = service.obterPorClientID("client-id");

        assertEquals("client-id", resultado.getClientId());
        verify(repository).findByClientId("client-id");
    }

    @Test
    void deveObterTokenDoOAuth() {
        String context = "http://localhost";

        when(repository.findByScopeInAndRedirectURIContaining(usuario.getRoles(), context)).thenReturn(client);

        try (MockedStatic<ServletUriComponentsBuilder> builderMockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            builderMockedStatic.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.build()).thenReturn(ServletUriComponentsBuilder.fromUriString(context).build());

            String expectedToken = "mocked-token";
            String jsonResponse = String.format("{\"access_token\":\"%s\"}", expectedToken);

            ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(responseEntity);

            OAuthTokenResponse token = service.getTokenFromOAuth(usuario);

            assertEquals(expectedToken, token.access_token());

            verify(repository).findByScopeInAndRedirectURIContaining(usuario.getRoles(), context);
            verify(restTemplate).postForEntity(eq(context + "/oauth2/token"), any(HttpEntity.class), eq(String.class));
        } catch (JsonProcessingException e) {
            Assertions.fail("Erro de processamento de JSON: " + e.getMessage());
        }
    }
}