package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaUsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioDTO;
import com.github.petervl80.acervoapi.controller.mappers.UsuarioMapper;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController implements GenericController {

    private final UsuarioService service;
    private final UsuarioMapper mapper;

    @PostMapping
    public ResponseEntity<Void> salvarMembro(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvarMembro(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> salvarAdministrador(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvarAdministrador(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/bibliotecarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> salvarBibliotecario(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvarBibliotecario(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaUsuarioDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,
            @RequestParam(value = "tamanhoPagina", defaultValue = "10")
            Integer tamanhoPagina) {

        Page<Usuario> paginaResultado = service.pesquisa(nome, role, pagina, tamanhoPagina);

        Page<ResultadoPesquisaUsuarioDTO> resultado = paginaResultado.map(mapper::toResultadoDTO);

        return ResponseEntity.ok(resultado);
    }
}
