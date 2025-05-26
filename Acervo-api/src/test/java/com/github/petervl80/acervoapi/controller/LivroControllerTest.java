package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.CadastroLivroDTO;
import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.github.petervl80.acervoapi.controller.mappers.LivroMapper;
import com.github.petervl80.acervoapi.model.DisponibilidadeEnum;
import com.github.petervl80.acervoapi.model.GeneroLivro;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LivroControllerTest {

    @Mock
    private LivroService service;

    @Mock
    private LivroMapper mapper;

    @InjectMocks
    private LivroController controller;

    private CadastroLivroDTO cadastroLivroDTO;
    private ResultadoPesquisaLivroDTO resultadoDto;
    private UUID idLivro;
    private Livro livro;

    @BeforeEach
    void setUp() {
        idLivro= UUID.randomUUID();
        cadastroLivroDTO = new CadastroLivroDTO("1234567890",
                "Dom Casmurro",
                LocalDate.now(),
                GeneroLivro.ROMANCE,
                "Machado de Assis",
                "Livro bom",
                "SGVsbG8gd29ybGQ=");
        livro = new Livro();
        livro.setId(idLivro);
        resultadoDto = new ResultadoPesquisaLivroDTO(
                idLivro,
                "1234567890",
                "Dom Casmurro",
                LocalDate.now(),
                GeneroLivro.ROMANCE,
                "Machado de Assis",
                "Livro bom",
                DisponibilidadeEnum.DISPONIVEL,
                "SGVsbG8gd29ybGQ=");
    }

    @Test
    void deveSalvarLivroComSucessoeRetornarStatusCreated() {
        livro.setIsbn("1234567890");
        livro.setTitulo("Dom Casmurro");
        livro.setDataPublicacao(LocalDate.now());
        livro.setGenero(GeneroLivro.ROMANCE);
        livro.setAutor("Machado de Assis");
        livro.setSumario("Livro clássico");
        livro.setImagem("Hello world".getBytes());

        when(mapper.toEntity(cadastroLivroDTO)).thenReturn(livro);

        try (MockedStatic<ServletUriComponentsBuilder> builderMockedStatic = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);

            builderMockedStatic.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(builder);
            when(builder.path("/{id}")).thenReturn(builder);
            when(builder.buildAndExpand(idLivro)).thenReturn(UriComponentsBuilder.fromUriString("http://localhost/clients/" + idLivro).build());

            ResponseEntity<Void> response = controller.salvar(cadastroLivroDTO);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());
            assertTrue(response.getHeaders().getLocation().toString().contains(idLivro.toString()));
            verify(mapper).toEntity(cadastroLivroDTO);
            verify(service).salvar(livro);
        }
    }

    @Test
    void deveRetornarLivroQuandoIdExistir() {
        when(service.obterPorId(livro.getId())).thenReturn(Optional.of(livro));
        when(mapper.toDTO(livro)).thenReturn(resultadoDto);

        ResponseEntity<ResultadoPesquisaLivroDTO> response = controller.obterDetalhes(livro.getId().toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resultadoDto, response.getBody());
    }

    @Test
    void deveRetornarNotFoundQuandoIdNaoExistir() {
        when(service.obterPorId(livro.getId())).thenReturn(Optional.empty());

        ResponseEntity<ResultadoPesquisaLivroDTO> response = controller.obterDetalhes(livro.getId().toString());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deveRetornarListaDeLivrosQuandoEncontrarResultados() {
        List<Livro> livros = List.of(livro);
        when(service.pesquisa(null, null, null, null, null)).thenReturn(livros);
        when(mapper.toDTO(livro)).thenReturn(resultadoDto);

        ResponseEntity<List<ResultadoPesquisaLivroDTO>> response = controller.pesquisar(null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(resultadoDto, response.getBody().getFirst());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoEncontrarResultados() {
        when(service.pesquisa(null, null, null, null, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ResultadoPesquisaLivroDTO>> response = controller.pesquisar(null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void deveDeletarLivroQuandoIdExistir() {
        when(service.obterPorId(idLivro)).thenReturn(Optional.of(livro));

        ResponseEntity<Object> response = controller.deletar(idLivro.toString());

        verify(service).deletar(livro);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deveRetornarNotFoundAoDeletarQuandoIdNaoExistir() {
        when(service.obterPorId(idLivro)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = controller.deletar(idLivro.toString());

        verify(service, never()).deletar(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveAtualizarLivroQuandoIdExistir() {
        when(service.obterPorId(idLivro)).thenReturn(Optional.of(livro));
        when(mapper.toEntity(cadastroLivroDTO)).thenReturn(livro);

        ResponseEntity<Object> response = controller.atualizar(idLivro.toString(), cadastroLivroDTO);

        verify(service).atualizar(any(Livro.class));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarQuandoIdNaoExistir() {
        when(service.obterPorId(idLivro)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = controller.atualizar(idLivro.toString(), cadastroLivroDTO);

        verify(service, never()).atualizar(any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}