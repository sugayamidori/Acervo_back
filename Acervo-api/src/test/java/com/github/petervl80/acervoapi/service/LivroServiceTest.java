package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.model.DisponibilidadeEnum;
import com.github.petervl80.acervoapi.model.GeneroLivro;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import com.github.petervl80.acervoapi.security.SecurityService;
import com.github.petervl80.acervoapi.validator.LivroValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @Mock
    private LivroValidator validator;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private LivroService service;

    private UUID idLivro;
    private UUID idMembro;
    private Usuario usuario;
    private Livro livro;

    @BeforeEach
    void setUp() {
        idLivro = UUID.randomUUID();
        idMembro = UUID.randomUUID();

        livro = new Livro();
        livro.setId(idLivro);
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        livro.setGenero(GeneroLivro.ROMANCE);
        livro.setDataPublicacao(LocalDate.of(1899, 1, 1));
        livro.setIsbn("1234567890");
        livro.setDataCadastro(LocalDateTime.now());
        livro.setDataAtualizacao(LocalDateTime.now());

        usuario = new Usuario();
        usuario.setLogin("pedro");
        usuario.setEmail("pedro@gmail.com");
        livro.setUsuario(usuario);
    }

    @Test
    void deveSalvarLivroComSucesso() {
        when(securityService.obterUsuarioLogado()).thenReturn(usuario);
        when(repository.save(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro salvo = service.salvar(livro);

        verify(validator).validar(livro);
        verify(repository).save(livro);
        assertEquals(livro, salvo);
        assertEquals(livro.getIsbn(), salvo.getIsbn());
        assertEquals(livro.getTitulo(), salvo.getTitulo());
        assertEquals(livro.getGenero(), salvo.getGenero());
        assertEquals(livro.getDataPublicacao(), salvo.getDataPublicacao());
        assertEquals(livro.getAutor(), salvo.getAutor());
        assertEquals(livro.getDataCadastro(), salvo.getDataCadastro());
        assertEquals(livro.getDataAtualizacao(), salvo.getDataAtualizacao());
        assertEquals(DisponibilidadeEnum.DISPONIVEL, salvo.getStatus());
        assertEquals(usuario, salvo.getUsuario());
    }

    @Test
    void deveObterLivroPorId() {
        when(repository.findById(idLivro)).thenReturn(Optional.of(livro));

        Optional<Livro> result = service.obterPorId(idLivro);

        assertTrue(result.isPresent());
        assertEquals(livro, result.get());
        assertEquals(livro.getId(), idLivro);
    }

    @Test
    void deveDeletarLivro() {
        service.deletar(livro);
        verify(repository).delete(livro);
    }

    @Test
    void devePesquisarLivroComTodosOsFiltros() {
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(livro));

        List<Livro> resultado = service.pesquisa(
                "1234567890",
                "Dom Casmurro",
                "Machado de Assis",
                GeneroLivro.ROMANCE,
                1899
        );

        assertFalse(resultado.isEmpty());
        verify(repository).findAll(any(Specification.class));

        Livro resultadoLivro = resultado.get(0);
        assertEquals(idLivro, resultadoLivro.getId());
        assertEquals("Dom Casmurro", resultadoLivro.getTitulo());
        assertEquals("Machado de Assis", resultadoLivro.getAutor());
        assertEquals("1234567890", resultadoLivro.getIsbn());
        assertEquals(GeneroLivro.ROMANCE, resultadoLivro.getGenero());
        assertEquals(1899, resultadoLivro.getDataPublicacao().getYear());
    }

    @Test
    void devePesquisarLivroComOsFiltrosParciais() {
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(livro));

        List<Livro> resultado = service.pesquisa(
                "1234567890",
                null,
                "Machado de Assis",
                null,
                1899
        );

        assertFalse(resultado.isEmpty());
        verify(repository).findAll(any(Specification.class));

        Livro resultadoLivro = resultado.get(0);
        assertEquals(idLivro, resultadoLivro.getId());
        assertEquals("Machado de Assis", resultadoLivro.getAutor());
        assertEquals("1234567890", resultadoLivro.getIsbn());
        assertEquals(1899, resultadoLivro.getDataPublicacao().getYear());
    }


    @Test
    void devePesquisarLivroSemFiltros() {
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(livro));

        List<Livro> resultado = service.pesquisa(null, null, null, null, null);

        assertFalse(resultado.isEmpty());
        verify(repository).findAll(any(Specification.class));
    }

    @Test
    void deveAtualizarLivroComIdValido() {
        livro.setTitulo("Outro titulo");
        when(repository.save(livro)).thenReturn(livro);

        service.atualizar(livro);

        verify(validator).validar(livro);
        ArgumentCaptor<Livro> captor = ArgumentCaptor.forClass(Livro.class);
        verify(repository).save(captor.capture());

        Livro livroSalvo = captor.getValue();
        assertEquals(livro.getId(), livroSalvo.getId());
        assertEquals("Outro titulo", livroSalvo.getTitulo());
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroSemId() {
        livro.setId(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(livro)
        );

        assertEquals("Livro não cadastrado", exception.getMessage());
        verify(validator, never()).validar(any());
        verify(repository, never()).save(any());
    }
}
