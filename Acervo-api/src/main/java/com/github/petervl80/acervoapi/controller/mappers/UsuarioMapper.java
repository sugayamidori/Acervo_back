package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaUsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioDTO;
import com.github.petervl80.acervoapi.controller.dto.UsuarioMembroDTO;
import com.github.petervl80.acervoapi.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);

    Usuario toEntity(UsuarioMembroDTO dto);

    UsuarioDTO toDTO(Usuario usuario);

    ResultadoPesquisaUsuarioDTO toResultadoDTO(Usuario usuario);
}
