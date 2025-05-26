package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaUsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioMembroDTO;
import com.github.petervl80.acervoapi.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-26T16:36:43-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public Usuario toEntity(UsuarioDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setNome( dto.nome() );
        usuario.setSenha( dto.senha() );
        usuario.setEmail( dto.email() );
        List<String> list = dto.roles();
        if ( list != null ) {
            usuario.setRoles( new ArrayList<String>( list ) );
        }

        return usuario;
    }

    @Override
    public Usuario toEntity(UsuarioMembroDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setNome( dto.nome() );
        usuario.setSenha( dto.senha() );
        usuario.setEmail( dto.email() );

        return usuario;
    }

    @Override
    public UsuarioDTO toDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        String nome = null;
        String senha = null;
        String email = null;
        List<String> roles = null;

        nome = usuario.getNome();
        senha = usuario.getSenha();
        email = usuario.getEmail();
        List<String> list = usuario.getRoles();
        if ( list != null ) {
            roles = new ArrayList<String>( list );
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO( nome, senha, email, roles );

        return usuarioDTO;
    }

    @Override
    public ResultadoPesquisaUsuarioDTO toResultadoDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UUID id = null;
        String nome = null;
        String email = null;
        List<String> roles = null;

        id = usuario.getId();
        nome = usuario.getNome();
        email = usuario.getEmail();
        List<String> list = usuario.getRoles();
        if ( list != null ) {
            roles = new ArrayList<String>( list );
        }

        ResultadoPesquisaUsuarioDTO resultadoPesquisaUsuarioDTO = new ResultadoPesquisaUsuarioDTO( id, nome, email, roles );

        return resultadoPesquisaUsuarioDTO;
    }
}
