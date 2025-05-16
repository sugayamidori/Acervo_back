package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Autor;
import com.github.petervl80.acervoapi.model.GeneroLivro;
import com.github.petervl80.acervoapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class AutorRepositoryTest {

    @Autowired
    AutorRepository repository;

    @Autowired
    LivroRepository livroRepository;

    @Test
    public void salvarTest() {
        Autor autor = new Autor();
        autor.setNome("José");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1950, 1, 31));

        var autoSalvo = repository.save(autor);
        System.out.println("Autor Salvo: " + autoSalvo);
    }

    @Test
    public void atualizarTeste() {
        var id = UUID.fromString("474c149a-e6bf-449d-b816-753a8e5bf758");

        Optional<Autor> possivelAutor = repository.findById(id);

        if(possivelAutor.isPresent()) {

            Autor autorEncontrado = possivelAutor.get();
            System.out.println("Dados do Autor: ");
            System.out.println(autorEncontrado);

            autorEncontrado.setDataNascimento(LocalDate.of(1960, 1, 30));

            repository.save(autorEncontrado);
        }
    }

    @Test
    public void listarTest() {
        List<Autor> lista = repository.findAll();
        lista.forEach(System.out::println);
    }

    @Test
    public void countTest() {
        System.out.println("Contagem de autores: " + repository.count());
    }

    @Test
    public void deleteTest() {
        var id = UUID.fromString("474c149a-e6bf-449d-b816-753a8e5bf758");
        repository.deleteById(id);
    }

    @Test
    void salvarAutorComLivrosTest() {
        Autor autor = new Autor();
        autor.setNome("Antonio");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1970, 8, 25));

        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setGenero(GeneroLivro.FANTASIA);
        livro.setTitulo("BIOGRAFIA");
        livro.setDataPublicacao(LocalDate.of(1999, 1, 2));
        livro.setAutor(autor);

        Livro livro2 = new Livro();
        livro2.setIsbn("90887-23584");
        livro2.setGenero(GeneroLivro.MISTERIO);
        livro2.setTitulo("OUTRA HISTORIA");
        livro2.setDataPublicacao(LocalDate.of(2000, 8, 23));
        livro2.setAutor(autor);

        autor.setLivros(new ArrayList<>());
        autor.getLivros().add(livro);
        autor.getLivros().add(livro2);

        repository.save(autor);

    }

    @Test
    void listarLivrosAutor() {
        UUID id = UUID.fromString("35ba5082-0e13-46f8-a0e1-501845c8e394");
        Autor autor = repository.findById(id).get();

        List<Livro> livrosLista = livroRepository.findByAutor(autor);
        autor.setLivros(livrosLista);

        autor.getLivros().forEach(System.out::println);
    }
}
