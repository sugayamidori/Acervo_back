package com.github.petervl80.acervoapi.controller;

import com.github.petervl80.acervoapi.controller.dto.CadastroLivroDTO;
import com.github.petervl80.acervoapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.github.petervl80.acervoapi.controller.mappers.LivroMapper;
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
    private ResultadoPesquisaLivroDTO pesquisaLivroDTO;
    private Livro livro;

    @BeforeEach
    void setUp() {
        cadastroLivroDTO = new CadastroLivroDTO("1234567890",
                "Dom Casmurro",
                LocalDate.now(),
                GeneroLivro.ROMANCE,
                "Machado de Assis",
                "Livro bom",
                "SGVsbG8gd29ybGQ=");
        livro = new Livro();
        livro.setId(UUID.randomUUID());
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
            when(builder.buildAndExpand(livro.getId())).thenReturn(UriComponentsBuilder.fromUriString("http://localhost/clients/" + livro.getId()).build());

            ResponseEntity<Void> response = controller.salvar(cadastroLivroDTO);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());
            assertTrue(response.getHeaders().getLocation().toString().contains(livro.getId().toString()));
            verify(mapper).toEntity(cadastroLivroDTO);
            verify(service).salvar(livro);
        }
    }

    @Test
    void obterDetalhes() {
    }

    @Test
    void deletar() {
    }

    @Test
    void pesquisar() {
    }

    @Test
    void atualizar() {
    }
}