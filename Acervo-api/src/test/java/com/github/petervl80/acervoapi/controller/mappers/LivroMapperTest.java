package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.CadastroLivroDTO;
import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.github.petervl80.acervoapi.model.GeneroLivro;
import com.github.petervl80.acervoapi.model.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LivroMapperTest {

    private LivroMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LivroMapper.class);
    }

    @Test
    void deveConverterCadastroLivroDTOParaLivro() {
        CadastroLivroDTO dto = new CadastroLivroDTO("1234567890",
                "Dom Casmurro",
                LocalDate.now(),
                GeneroLivro.ROMANCE,
                "Machado de Assis",
                "Livro clássico",
                "SGVsbG8gd29ybGQ="); //"Hello world" em base64

        Livro entityToFill = mapper.toEntity(dto);

        assertNotNull(entityToFill);
        assertEquals(dto.isbn(), entityToFill.getIsbn());
        assertEquals(dto.titulo(), entityToFill.getTitulo());
        assertEquals(dto.dataPublicacao(), entityToFill.getDataPublicacao());
        assertEquals(dto.genero(), entityToFill.getGenero());
        assertEquals(dto.autor(), entityToFill.getAutor());
        assertEquals(dto.sumario(), entityToFill.getSumario());
        assertArrayEquals("Hello world".getBytes(), entityToFill.getImagem());
    }

    @Test
    void deveConverterLivroParaResultadoPesquisaLivroDTO() {
        Livro entity = new Livro();
        entity.setIsbn("1234567890");
        entity.setTitulo("Dom Casmurro");
        entity.setDataPublicacao(LocalDate.now());
        entity.setGenero(GeneroLivro.ROMANCE);
        entity.setAutor("Machado de Assis");
        entity.setSumario("Livro clássico");
        entity.setImagem("Hello world".getBytes());

        ResultadoPesquisaLivroDTO dtoToFill = mapper.toDTO(entity);

        assertNotNull(dtoToFill);
        assertEquals(entity.getIsbn(), dtoToFill.isbn());
        assertEquals(entity.getTitulo(), dtoToFill.titulo());
        assertEquals(entity.getDataPublicacao(), dtoToFill.dataPublicacao());
        assertEquals(entity.getGenero(), dtoToFill.genero());
        assertEquals(entity.getAutor(), dtoToFill.autor());
        assertEquals(entity.getSumario(), dtoToFill.sumario());
        assertEquals("SGVsbG8gd29ybGQ=", dtoToFill.imagem());
    }

    @Test
    void deveRetornarNullQuandoImagemBase64ForNull() {
        CadastroLivroDTO dto = new CadastroLivroDTO("1234567890",
                "Dom Casmurro",
                LocalDate.now(),
                GeneroLivro.ROMANCE,
                "Machado de Assis",
                "Livro clássico",
                null);

        Livro entity = mapper.toEntity(dto);
        assertNull(entity.getImagem());
    }

    @Test
    void deveRetornarNullQuandoImagemBytesForNull() {
        Livro entity = new Livro();
        entity.setIsbn("1234567890");
        entity.setTitulo("Dom Casmurro");
        entity.setDataPublicacao(LocalDate.now());
        entity.setGenero(GeneroLivro.ROMANCE);
        entity.setAutor("Machado de Assis");
        entity.setSumario("Livro clássico");

        ResultadoPesquisaLivroDTO dto = mapper.toDTO(entity);
        assertNull(dto.imagem());
    }

}