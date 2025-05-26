package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.exceptions.OperecaoNaoPermitidaException;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.model.StatusEmprestimo;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private UUID idLivro;
    private UUID idMembro;
    private Usuario membro;
    private Usuario bibliotecario;
    private Livro livro;

    @BeforeEach
    void setup() {
        idLivro = UUID.randomUUID();
        idMembro = UUID.randomUUID();

        membro = new Usuario();
        membro.setId(idMembro);
        membro.setLogin("membro");
        membro.setRoles(List.of("MEMBRO"));

        bibliotecario = new Usuario();
        bibliotecario.setLogin("bibliotecario");
        bibliotecario.setRoles(List.of("BIBLIOTECARIO"));

        livro = new Livro();
        livro.setId(idLivro);
        livro.setTitulo("Livro de Teste");
    }

    @Test
    void deveRegistrarEmprestimoComSucesso() {
        // Arrange
        when(usuarioRepository.findById(idMembro)).thenReturn(Optional.of(membro));
        when(usuarioRepository.findByLogin("bibliotecario")).thenReturn(bibliotecario);
        when(livroRepository.findById(idLivro)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Emprestimo emprestimo = emprestimoService.registrarEmprestimo(idLivro, idMembro);

        // Assert
        assertNotNull(emprestimo);
        assertEquals(StatusEmprestimo.RESERVADO, emprestimo.getStatus());
        assertEquals("Livro de Teste", emprestimo.getLivro().getTitulo());
        assertEquals(membro.getId(), emprestimo.getMembro().getId());
        assertEquals(bibliotecario.getLogin(), emprestimo.getRegistradoPor().getLogin());
        assertEquals(LocalDate.now().plusDays(7), emprestimo.getDataLimiteDevolucao());

        verify(emprestimoRepository).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForMembro() {
        // Arrange
        membro.setRoles(List.of("ADMINISTRADOR"));
        when(usuarioRepository.findById(idMembro)).thenReturn(Optional.of(membro));
        when(usuarioRepository.findByLogin("bibliotecario")).thenReturn(bibliotecario);
        when(livroRepository.findById(idLivro)).thenReturn(Optional.of(livro));

        // Act + Assert
        assertThrows(OperecaoNaoPermitidaException.class, () ->
                emprestimoService.registrarEmprestimo(idLivro, idMembro));
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoExiste() {
        when(usuarioRepository.findById(idMembro)).thenReturn(Optional.of(membro));
        when(usuarioRepository.findByLogin("bibliotecario")).thenReturn(bibliotecario);
        when(livroRepository.findById(idLivro)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                emprestimoService.registrarEmprestimo(idLivro, idMembro));
    }
}
