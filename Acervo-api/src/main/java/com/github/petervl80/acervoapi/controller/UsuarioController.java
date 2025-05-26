package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.*;
import com.github.petervl80.acervoapi.controller.mappers.UsuarioMapper;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.service.ClientService;
import com.github.petervl80.acervoapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController implements GenericController {

    private final UsuarioService service;
    private final ClientService clientService;
    private final UsuarioMapper mapper;

    @PostMapping("/membros")
    public ResponseEntity<Void> salvarMembro(@RequestBody @Valid UsuarioMembroDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvarMembro(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> salvarUsuario(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvarUsuario(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BIBLIOTECARIO')")
    public ResponseEntity<List<ResultadoPesquisaUsuarioDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "role", required = false) String role) {

        List<Usuario> paginaResultado = service.pesquisa(nome, role);

        List<ResultadoPesquisaUsuarioDTO> resultado = paginaResultado.stream().map(mapper::toResultadoDTO).toList();

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = service.autenticar(dto);
            OAuthTokenResponse token = clientService.getTokenFromOAuth(usuario);

            LoginResponse response = new LoginResponse(
                    usuario.getId(),
                    token.access_token(),
                    token.scope(),
                    token.token_type(),
                    token.expires_in());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}