package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {
}
