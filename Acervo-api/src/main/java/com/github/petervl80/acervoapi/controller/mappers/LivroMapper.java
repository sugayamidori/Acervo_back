package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.CadastroLivroDTO;
import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.github.petervl80.acervoapi.model.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface LivroMapper {

    @Mapping(target = "imagem", source = "imagem", qualifiedByName = "base64ToBytes")
    Livro toEntity(CadastroLivroDTO dto);

    @Mapping(target = "imagem", source = "imagem", qualifiedByName = "bytesToBase64")
    ResultadoPesquisaLivroDTO toDTO(Livro livro);

    @Named("base64ToBytes")
    default byte[] base64ToBytes(String base64) {
        if (base64 == null) {
            return null;
        }
        return Base64.getDecoder().decode(base64);
    }

    @Named("bytesToBase64")
    default String bytesToBase64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(bytes);
    }
}
