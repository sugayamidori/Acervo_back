package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Autor;
import com.github.petervl80.acervoapi.model.GeneroLivro;
import com.github.petervl80.acervoapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class LivroRepositoryTest {

    @Autowired
    LivroRepository repository;

    @Autowired
    AutorRepository autorRepository;

    @Test
    void salvarTest() {
        Livro livro = new Livro();
        livro.setIsbn("90987-84874");
        livro.setPreco(BigDecimal.valueOf(120));
        livro.setGenero(GeneroLivro.FANTASIA);
        livro.setTitulo("SLA");
        livro.setDataPublicacao(LocalDate.of(1985, 1, 2));

        Autor autor = autorRepository
                .findById(UUID.fromString("b838e407-c656-450b-be99-979a0c532076"))
                .orElse(null);

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void salvarCascadeTest() {
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("OUTRO LIVRO");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = new Autor();
        autor.setNome("João");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951, 1, 30));

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    @Transactional
    void buscarLivroTest() {
        UUID id = UUID.fromString("a90ae5eb-8628-4643-9a5b-01297d49f43f");
        Livro livro = repository.findById(id).orElse(null);
        System.out.println("Livro: ");
        System.out.println(livro.getTitulo());
        System.out.println("Autor: ");
        System.out.println(livro.getAutor().getNome());
    }

    @Test
    void pesquisarPorTituloTest() {
        List<Livro> lista = repository.findByTituloLike("BIO");
        lista.forEach(System.out::println);
    }

    @Test
    void pesquisarPorISBNTest() {
        Optional<Livro> livro = repository.findByIsbn("");
        livro.ifPresent(System.out::println);
    }
}