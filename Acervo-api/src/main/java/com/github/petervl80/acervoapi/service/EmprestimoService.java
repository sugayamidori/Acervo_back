package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoListagemDTO;
import com.github.petervl80.acervoapi.exceptions.OperecaoNaoPermitidaException;
import com.github.petervl80.acervoapi.model.*;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final SecurityService securityService;

    private static final int PRAZO_PADRAO_DIAS = 7;
    private static final BigDecimal VALOR_MULTA_POR_DIA = new BigDecimal("2.50");

    public Emprestimo registrarEmprestimo(UUID idLivro, UUID idUsuario) {
        Usuario usuario = usuarioRepository.findByNome("testeMember");
        Livro livro = livroRepository.findById(idLivro).orElseThrow();

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setMembro(usuario);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.RESERVADO);

        return repository.save(emprestimo);
    }
    @PreAuthorize("hasRole('BIBLIOTECARIO') or hasRole('ADMINISTRADOR')")
    public Emprestimo liberarEmprestimo(UUID idEmprestimo) {

        Emprestimo emprestimo = repository.findById(idEmprestimo).orElseThrow();

        if (emprestimo.getStatus() != StatusEmprestimo.RESERVADO) {
            throw new OperecaoNaoPermitidaException("Somente reservas podem ser liberadas.");
        }
        Usuario bibliotecario = securityService.obterUsuarioLogado();
        emprestimo.setRegistradoPor(bibliotecario);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataLimiteDevolucao(LocalDate.now().plusDays(PRAZO_PADRAO_DIAS));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        return repository.save(emprestimo);
    }
    @PreAuthorize("hasRole('BIBLIOTECARIO')")
    public Emprestimo devolver(UUID idEmprestimo) {
        Emprestimo emprestimo = repository.findById(idEmprestimo).orElseThrow();
        LocalDate dataDevolucao = LocalDate.now();
        emprestimo.setDataDevolucao(dataDevolucao);

        if (dataDevolucao.isAfter(emprestimo.getDataLimiteDevolucao())) {
            long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataLimiteDevolucao(), dataDevolucao);
            BigDecimal multa = VALOR_MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
            Multa novaMulta = Multa.builder()
                    .emprestimo(emprestimo)
                    .valor(multa)
                    .status(StatusMulta.PENDENTE)
                    .dataGeracao(LocalDate.now())
                    .build();

            emprestimo.setMulta(novaMulta);
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO_COM_ATRASO);
        } else {
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO_NO_PRAZO);
        }

        return repository.save(emprestimo);
    }
    public Emprestimo reservarLivro(UUID idLivro) {
        Usuario membro = securityService.obterUsuarioLogado();
        Livro livro = livroRepository.findById(idLivro).orElseThrow();

        Emprestimo reserva = new Emprestimo();
        reserva.setLivro(livro);
        reserva.setMembro(membro);
        reserva.setDataEmprestimo(LocalDate.now());
        reserva.setStatus(StatusEmprestimo.RESERVADO);

        return repository.save(reserva);
    }
//    @PreAuthorize("hasRole('BIBLIOTECARIO') or hasRole('ADMINISTRADOR')")
    public List<EmprestimoListagemDTO> listarEmprestimos() {
        List<Emprestimo> emprestimos = repository.findAll();

        return emprestimos.stream().map(emprestimo -> {
            Long diasAtraso = null;
            BigDecimal valorMulta = null;

            if (emprestimo.getDataLimiteDevolucao() != null && emprestimo.getDataDevolucao() != null
                    && emprestimo.getDataDevolucao().isAfter(emprestimo.getDataLimiteDevolucao())) {
                diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataLimiteDevolucao(), emprestimo.getDataDevolucao());
            }

            if (emprestimo.getMulta() != null) {
                valorMulta = emprestimo.getMulta().getValor();
            }

            return EmprestimoListagemDTO.builder()
                    .id(emprestimo.getId())
                    .livroTitulo(emprestimo.getLivro().getTitulo())
                    .usuarioNome(emprestimo.getMembro().getNome())
                    .dataDevolucao(emprestimo.getDataDevolucao())
                    .diasAtraso(diasAtraso)
                    .valorMulta(valorMulta)
                    .build();
        }).toList();
    }
}
