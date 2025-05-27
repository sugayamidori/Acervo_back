package com.github.petervl80.acervoapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.petervl80.acervoapi.controller.dto.*;
import com.github.petervl80.acervoapi.controller.mappers.UsuarioMapper;
import com.github.petervl80.acervoapi.exceptions.UsuarioNaoEncontradoException;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.service.ClientService;
import com.github.petervl80.acervoapi.service.UsuarioService;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @Mock
    private ClientService clientService;

    @Mock
    private UsuarioMapper mapper;

    @InjectMocks
    private UsuarioController controller;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioMembroDTO usuarioMembroDTO;
    private LoginUsuarioDTO loginUsuarioDTO;
    private ResultadoPesquisaUsuarioDTO resultadoDTO;
    private UUID idUsuario;

    @BeforeEach
    void setUp() {
        idUsuario = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(idUsuario);

        usuarioDTO = new UsuarioDTO(
                "admin",
                "1234",
                "admin@gmail.com",
                List.of("ADMINISTRADOR"));

        usuarioMembroDTO = new UsuarioMembroDTO(
                "membro",
                "1234",
                "membro@gmail.com");

        loginUsuarioDTO = new LoginUsuarioDTO(
                "admin",
                "1234"
        );

        resultadoDTO = new ResultadoPesquisaUsuarioDTO(
                idUsuario,
                "admin",
                "admin@gmail.com",
                List.of("ADMINISTRADOR"));
    }

    @Test
    void deveSalvarMembroComSucessoERetornarStatusCreated() {
        when(mapper.toEntity(usuarioMembroDTO)).thenReturn(usuario);

        try (MockedStatic<ServletUriComponentsBuilder> builderMockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            builderMockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(builder);
            when(builder.path("/{id}")).thenReturn(builder);
            when(builder.buildAndExpand(idUsuario)).thenReturn(UriComponentsBuilder.fromUriString("http://localhost/usuarios/" + idUsuario).build());

            ResponseEntity<Void> response = controller.salvarMembro(usuarioMembroDTO);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());
            verify(service).salvarMembro(usuario);
        }
    }

    @Test
    void deveSalvarUsuarioComSucessoERetornarStatusCreated() {
        when(mapper.toEntity(usuarioDTO)).thenReturn(usuario);

        try (MockedStatic<ServletUriComponentsBuilder> builderMockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            builderMockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(builder);
            when(builder.path("/{id}")).thenReturn(builder);
            when(builder.buildAndExpand(idUsuario)).thenReturn(UriComponentsBuilder.fromUriString("http://localhost/usuarios/" + idUsuario).build());

            ResponseEntity<Void> response = controller.salvarUsuario(usuarioDTO);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());
            verify(service).salvarUsuario(usuario);
        }
    }

    @Test
    void deveRetornarListaDeUsuariosQuandoEncontrarResultados() {
        List<Usuario> usuarios = List.of(usuario);
        when(service.pesquisa("admin", "ADMINISTRADOR")).thenReturn(usuarios);
        when(mapper.toResultadoDTO(usuario)).thenReturn(resultadoDTO);

        ResponseEntity<List<ResultadoPesquisaUsuarioDTO>> response = controller.pesquisar("admin", "ADMINISTRADOR");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(resultadoDTO, response.getBody().getFirst());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoEncontrarUsuarios() {
        when(service.pesquisa(null, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ResultadoPesquisaUsuarioDTO>> response = controller.pesquisar(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void deveRetornarTokenValidoQuandoLoginForBemSucedido() throws JsonProcessingException {
        when(service.autenticar(loginUsuarioDTO)).thenReturn(usuario);

        OAuthTokenResponse token = new OAuthTokenResponse("token123", "read", "Bearer", 3600);
        when(clientService.getTokenFromOAuth(usuario, loginUsuarioDTO.senha())).thenReturn(token);

        ResponseEntity<LoginResponse> response = controller.login(loginUsuarioDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("token123", response.getBody().access_token());
    }

    @Test
    void deveRetornarNotFoundQuandoLoginFalhar() throws JsonProcessingException {
        when(service.autenticar(loginUsuarioDTO)).thenThrow(new UsuarioNaoEncontradoException("Usuário e/ou senha incorretos"));

        ResponseEntity<LoginResponse> response = controller.login(loginUsuarioDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
