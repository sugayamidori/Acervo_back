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

        Livro livro = mapper.toEntity(dto);

        assertNotNull(livro);
        assertEquals(dto.isbn(), livro.getIsbn());
        assertEquals(dto.titulo(), livro.getTitulo());
        assertEquals(dto.dataPublicacao(), livro.getDataPublicacao());
        assertEquals(dto.genero(), livro.getGenero());
        assertEquals(dto.autor(), livro.getAutor());
        assertEquals(dto.sumario(), livro.getSumario());
        assertArrayEquals("Hello world".getBytes(), livro.getImagem());
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

        ResultadoPesquisaLivroDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getIsbn(), dto.isbn());
        assertEquals(entity.getTitulo(), dto.titulo());
        assertEquals(entity.getDataPublicacao(), dto.dataPublicacao());
        assertEquals(entity.getGenero(), dto.genero());
        assertEquals(entity.getAutor(), dto.autor());
        assertEquals(entity.getSumario(), dto.sumario());
        assertEquals("SGVsbG8gd29ybGQ=", dto.imagem());
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

        Livro livro = mapper.toEntity(dto);
        assertNull(livro.getImagem());
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