package com.github.petervl80.acervoapi.validator;

import com.github.petervl80.acervoapi.exceptions.CampoInvalidoException;
import com.github.petervl80.acervoapi.exceptions.RegistroDuplicadoException;
import com.github.petervl80.acervoapi.model.Livro;
import com.github.petervl80.acervoapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivroValidator {

    private final LivroRepository repository;

    public void validar(Livro livro) {
        if(existeLivroComIsbn(livro)) {
            throw new RegistroDuplicadoException("ISBN já cadastrado");
        }
    }

    private boolean existeLivroComIsbn(Livro livro) {
        Optional<Livro> livroEncontrado = repository.findByIsbn(livro.getIsbn());

        if(livro.getId() == null) {
            return livroEncontrado.isPresent();
        }

        return livroEncontrado
                .map(Livro::getId)
                .stream()
                .anyMatch( id -> !id.equals(livro.getId()));
    }
}
