package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.PagamentoRequest;
import com.github.petervl80.acervoapi.exceptions.OperecaoNaoPermitidaException;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.model.StatusEmprestimo;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.EmprestimoRepository;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();

        Livro livro = livroRepository.findById(idLivro).orElseThrow();

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setMembro(usuario);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.RESERVADO);

        return repository.save(emprestimo);
    }


    public Emprestimo liberarEmprestimo(UUID idEmprestimo) {
        Usuario bibliotecario = securityService.obterUsuarioLogado();

        if (bibliotecario == null || !(bibliotecario.getRoles().contains("BIBLIOTECARIO") || bibliotecario.getRoles().contains("ADMINISTRADOR"))) {
            throw new OperecaoNaoPermitidaException("Somente bibliotecários podem liberar empréstimos.");
        }

        Emprestimo emprestimo = repository.findById(idEmprestimo).orElseThrow();

        if (emprestimo.getStatus() != StatusEmprestimo.RESERVADO) {
            throw new OperecaoNaoPermitidaException("Somente reservas podem ser liberadas.");
        }

        emprestimo.setRegistradoPor(bibliotecario);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataLimiteDevolucao(LocalDate.now().plusDays(PRAZO_PADRAO_DIAS));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        return repository.save(emprestimo);
    }


    public Emprestimo devolver(UUID idEmprestimo) {


        Emprestimo emprestimo = repository.findById(idEmprestimo).orElseThrow();
        LocalDate dataDevolucao = LocalDate.now();
        emprestimo.setDataDevolucao(dataDevolucao);

        if (dataDevolucao.isAfter(emprestimo.getDataLimiteDevolucao())) {
            long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataLimiteDevolucao(), dataDevolucao);
            BigDecimal multa = VALOR_MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
            emprestimo.setMulta(multa);
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO_COM_ATRASO);
        } else {
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO_NO_PRAZO);
        }

        return repository.save(emprestimo);
    }

    public Emprestimo realizarPagamento(UUID idEmprestimo, PagamentoRequest request) {
        Emprestimo emprestimo = repository.findById(idEmprestimo).orElseThrow();

        if (emprestimo.getStatus() != StatusEmprestimo.DEVOLVIDO_COM_ATRASO) {
            throw new OperecaoNaoPermitidaException("Não há multa pendente para este empréstimo.");
        }

        if (request.metodo() == null) {
            throw new IllegalArgumentException("Método de pagamento obrigatório.");
        }

        switch (request.metodo()) {
            case CARTAO -> {
                if (request.numeroCartao() == null || request.numeroCartao().length() != 16) {
                    throw new IllegalArgumentException("Número de cartão inválido.");
                }
                if (request.cvv() == null || request.cvv().length() != 3) {
                    throw new IllegalArgumentException("CVV inválido.");
                }
            }
            case PIX, BOLETO -> {
                if (request.valor() == null || request.valor().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Valor de pagamento inválido.");
                }
            }
            default -> throw new IllegalArgumentException("Método de pagamento não suportado.");
        }

        emprestimo.setDataPagamentoMulta(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.MULTA_PAGA);

        return repository.save(emprestimo);
    }
    public Emprestimo reservarLivro(UUID idLivro) {
        Usuario membro = securityService.obterUsuarioLogado();

        if (membro == null || !membro.getRoles().contains("MEMBRO")) {
            throw new OperecaoNaoPermitidaException("Somente membros podem reservar livros.");
        }

        Livro livro = livroRepository.findById(idLivro).orElseThrow();

        Emprestimo reserva = new Emprestimo();
        reserva.setLivro(livro);
        reserva.setMembro(membro);
        reserva.setDataEmprestimo(LocalDate.now());
        reserva.setStatus(StatusEmprestimo.RESERVADO);

        return repository.save(reserva);
    }



}
