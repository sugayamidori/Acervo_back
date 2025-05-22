package com.github.petervl80.acervoapi.controller.dto;

import com.github.petervl80.acervoapi.model.GeneroLivro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.ISBN;

import java.time.LocalDate;

public record CadastroLivroDTO(
        @ISBN
        @NotBlank(message = "Campo obrigatório")
        String isbn,
        @NotBlank(message = "Campo obrigatório")
        String titulo,
        @NotNull(message = "Campo obrigatório")
        @Past(message = "Não pode ser uma data futura")
        LocalDate dataPublicacao,
        GeneroLivro genero,
        @NotNull(message = "Campo obrigatório")
        String autor,
        @NotNull(message = "Campo obrigatório")
        String sumario,
        String imagem) {
}
