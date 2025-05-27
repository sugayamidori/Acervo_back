package com.github.petervl80.acervoapi.controller.mappers;

import com.github.petervl80.acervoapi.controller.dto.EmprestimoDTO;
import com.github.petervl80.acervoapi.model.Emprestimo;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.model.StatusEmprestimo;
import com.github.petervl80.acervoapi.model.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmprestimoMapperTest {

    private final EmprestimoMapper mapper = Mappers.getMapper(EmprestimoMapper.class);

    @Test
    void deveMapearEmprestimoParaEmprestimoDTO() {

        UUID idEmprestimo = UUID.randomUUID();
        UUID idLivro = UUID.randomUUID();
        UUID idUsuario = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);

        Livro livro = new Livro();
        livro.setId(idLivro);
        livro.setTitulo("Livro Teste");

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(idEmprestimo);
        emprestimo.setLivro(livro);
        emprestimo.setMembro(usuario);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(7));
        emprestimo.setDataLimiteDevolucao(LocalDate.now().plusDays(10));
        emprestimo.setStatus(StatusEmprestimo.EM_VIGENCIA);

        EmprestimoDTO dto = mapper.toDTO(emprestimo);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(idEmprestimo);
        assertThat(dto.membroId()).isEqualTo(idUsuario);
        assertThat(dto.livroId()).isEqualTo(idLivro);
        assertThat(dto.livroTitulo()).isEqualTo("Livro Teste");
        assertThat(dto.dataEmprestimo()).isEqualTo(emprestimo.getDataEmprestimo());
        assertThat(dto.dataDevolucao()).isEqualTo(emprestimo.getDataDevolucao());
        assertThat(dto.dataLimiteDevolucao()).isEqualTo(emprestimo.getDataLimiteDevolucao());
        assertThat(dto.status()).isEqualTo(StatusEmprestimo.EM_VIGENCIA);
    }
}
