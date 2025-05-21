package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.AutorDTO;
import com.github.petervl80.acervoapi.controller.dto.CadastroLivroDTO;
import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.github.petervl80.acervoapi.model.GeneroLivro;
import com.github.petervl80.acervoapi.model.Livro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-20T21:20:56-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class LivroMapperImpl extends LivroMapper {

    @Autowired
    private AutorMapper autorMapper;

    @Override
    public Livro toEntity(CadastroLivroDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Livro livro = new Livro();

        livro.setIsbn( dto.isbn() );
        livro.setTitulo( dto.titulo() );
        livro.setDataPublicacao( dto.dataPublicacao() );
        livro.setGenero( dto.genero() );

        livro.setAutor( autorRepository.findById(dto.idAutor()).orElse(null) );

        return livro;
    }

    @Override
    public ResultadoPesquisaLivroDTO toDTO(Livro livro) {
        if ( livro == null ) {
            return null;
        }

        UUID id = null;
        String isbn = null;
        String titulo = null;
        LocalDate dataPublicacao = null;
        GeneroLivro genero = null;
        AutorDTO autor = null;

        id = livro.getId();
        isbn = livro.getIsbn();
        titulo = livro.getTitulo();
        dataPublicacao = livro.getDataPublicacao();
        genero = livro.getGenero();
        autor = autorMapper.toDTO( livro.getAutor() );

        BigDecimal preco = null;

        ResultadoPesquisaLivroDTO resultadoPesquisaLivroDTO = new ResultadoPesquisaLivroDTO( id, isbn, titulo, dataPublicacao, genero, preco, autor );

        return resultadoPesquisaLivroDTO;
    }
}
