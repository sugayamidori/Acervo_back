package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaUsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioMembroDTO;
import com.github.petervl80.acervoapi.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UsuarioMapper.class);
    }

    @Test
    void deveConverterUsuarioDTOParaUsuario() {
        UsuarioDTO dto = new UsuarioDTO("teste",
                "teste123",
                "teste@gmail.com",
                List.of("TESTE"));

        Usuario usuario = mapper.toEntity(dto);

        assertNotNull(usuario);
        assertEquals(dto.nome(), usuario.getNome());
        assertEquals(dto.senha(), usuario.getSenha());
        assertEquals(dto.email(), usuario.getEmail());
        assertEquals(dto.roles(), usuario.getRoles());
    }

    @Test
    void deveConverterUsuarioMembroDTOParaUsuario() {
        UsuarioMembroDTO dto = new UsuarioMembroDTO("teste",
                "teste123",
                "teste@gmail.com");

        Usuario usuario = mapper.toEntity(dto);

        assertNotNull(usuario);
        assertEquals(dto.nome(), usuario.getNome());
        assertEquals(dto.senha(), usuario.getSenha());
        assertEquals(dto.email(), usuario.getEmail());
    }

    @Test
    void deveConverterUsuarioParaUsuarioDTO() {
        Usuario usuario = new Usuario();
        usuario.setNome("teste");
        usuario.setSenha("teste123");
        usuario.setEmail("teste@gmail.com");
        usuario.setRoles(List.of("TESTE"));

        UsuarioDTO dto = mapper.toDTO(usuario);

        assertNotNull(usuario);
        assertEquals(usuario.getNome(), dto.nome());
        assertEquals(usuario.getSenha(), dto.senha());
        assertEquals(usuario.getEmail(), dto.email());
        assertEquals(usuario.getRoles(), dto.roles());
    }

    @Test
    void deveConverterUsuarioParaResultadoPesquisaUsuarioDTO() {
        Usuario usuario = new Usuario();
        usuario.setNome("teste");
        usuario.setSenha("teste123");
        usuario.setEmail("teste@gmail.com");
        usuario.setRoles(List.of("TESTE"));

        ResultadoPesquisaUsuarioDTO dto = mapper.toResultadoDTO(usuario);

        assertNotNull(usuario);
        assertEquals(usuario.getNome(), dto.nome());
        assertEquals(usuario.getEmail(), dto.email());
        assertEquals(usuario.getRoles(), dto.roles());
    }
}