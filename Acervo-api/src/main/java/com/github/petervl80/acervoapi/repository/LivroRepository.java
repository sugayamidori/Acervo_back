package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Autor;
import com.github.petervl80.acervoapi.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID>, JpaSpecificationExecutor<Livro > {

    List<Livro> findByAutor(Autor autor);

    List<Livro> findByTituloLike(String titulo);

    Optional<Livro> findByIsbn(String isbn);

    boolean existsByAutor(Autor autor);
}
