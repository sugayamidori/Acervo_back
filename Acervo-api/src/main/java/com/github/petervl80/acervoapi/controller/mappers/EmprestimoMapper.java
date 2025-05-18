package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoDTO;
import com.github.petervl80.acervoapi.model.Emprestimo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

    @Mapping(source = "membro.id", target = "membroId")
    @Mapping(source = "livro.id", target = "livroId")
    @Mapping(source = "livro.titulo", target = "livroTitulo")
    EmprestimoDTO toDTO(Emprestimo emprestimo);
}
