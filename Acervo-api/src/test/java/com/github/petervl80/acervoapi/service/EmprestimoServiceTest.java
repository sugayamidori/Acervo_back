package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoListagemDTO;
import com.github.petervl80.acervoapi.exceptions.OperecaoNaoPermitidaException;
import com.github.petervl80.acervoapi.model.*;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EmprestimoServiceTest {

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Mock
    private EmprestimoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private SecurityService securityService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarEmprestimo_deveCriarEmprestimo() {
        UUID livroId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("testeMember");

        Livro livro = new Livro();
        livro.setId(livroId);
        livro.setTitulo("Livro Teste");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livro));
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emprestimo emprestimo = emprestimoService.registrarEmprestimo(livroId, usuarioId);

        assertThat(emprestimo.getLivro()).isEqualTo(livro);
        assertThat(emprestimo.getMembro()).isEqualTo(usuario);
        assertThat(emprestimo.getDataEmprestimo()).isEqualTo(LocalDate.now());
        assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.RESERVADO);

        verify(repository).save(emprestimo);
    }

    @Test
    void liberarEmprestimo_deveLiberarEmprestimo() {
        UUID idEmprestimo = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setStatus(StatusEmprestimo.RESERVADO);

        Usuario bibliotecario = new Usuario();
        bibliotecario.setId(UUID.randomUUID());
        bibliotecario.setNome("Bib");

        when(repository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(securityService.obterUsuarioLogado()).thenReturn(bibliotecario);
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emprestimo resultado = emprestimoService.liberarEmprestimo(idEmprestimo);

        assertThat(resultado.getStatus()).isEqualTo(StatusEmprestimo.EM_VIGENCIA);
        assertThat(resultado.getRegistradoPor()).isEqualTo(bibliotecario);
        assertThat(resultado.getDataLimiteDevolucao()).isEqualTo(LocalDate.now().plusDays(7));

        verify(repository).save(emprestimo);
    }

    @Test
    void liberarEmprestimo_quandoNaoReservado_deveLancarExcecao() {
        UUID idEmprestimo = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setStatus(StatusEmprestimo.EM_VIGENCIA);

        when(repository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));

        assertThatThrownBy(() -> emprestimoService.liberarEmprestimo(idEmprestimo))
                .isInstanceOf(OperecaoNaoPermitidaException.class)
                .hasMessage("Somente reservas podem ser liberadas.");
    }

    @Test
    void devolver_quandoDevolvidoNoPrazo() {
        UUID idEmprestimo = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataLimiteDevolucao(LocalDate.now().plusDays(1));

        when(repository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emprestimo resultado = emprestimoService.devolver(idEmprestimo);

        assertThat(resultado.getStatus()).isEqualTo(StatusEmprestimo.DEVOLVIDO_NO_PRAZO);
        assertThat(resultado.getDataDevolucao()).isEqualTo(LocalDate.now());
        assertThat(resultado.getMulta()).isNull();
    }

    @Test
    void devolver_quandoDevolvidoComAtraso() {
        UUID idEmprestimo = UUID.randomUUID();
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataLimiteDevolucao(LocalDate.now().minusDays(3));

        when(repository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emprestimo resultado = emprestimoService.devolver(idEmprestimo);

        assertThat(resultado.getStatus()).isEqualTo(StatusEmprestimo.DEVOLVIDO_COM_ATRASO);
        assertThat(resultado.getMulta()).isNotNull();
        assertThat(resultado.getMulta().getValor()).isEqualTo(new BigDecimal("7.50"));
    }


    @Test
    void listarEmprestimos_deveRetornarListaComAtrasoEMulta() {
        Livro livro = new Livro();
        livro.setTitulo("Livro Teste");

        Usuario membro = new Usuario();
        membro.setNome("Membro");

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(UUID.randomUUID());
        emprestimo.setLivro(livro);
        emprestimo.setMembro(membro);
        emprestimo.setDataLimiteDevolucao(LocalDate.now().minusDays(2));
        emprestimo.setDataDevolucao(LocalDate.now());

        Multa multa = new Multa();
        multa.setValor(new BigDecimal("5.00"));
        emprestimo.setMulta(multa);

        when(repository.findAll()).thenReturn(List.of(emprestimo));

        List<EmprestimoListagemDTO> resultado = emprestimoService.listarEmprestimos();

        assertThat(resultado).hasSize(1);
        EmprestimoListagemDTO dto = resultado.getFirst();
        assertThat(dto.getLivroTitulo()).isEqualTo("Livro Teste");
        assertThat(dto.getUsuarioNome()).isEqualTo("Membro");
        assertThat(dto.getDiasAtraso()).isEqualTo(2);
        assertThat(dto.getValorMulta()).isEqualTo(new BigDecimal("5.00"));
    }
}
