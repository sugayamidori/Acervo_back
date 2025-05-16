package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {

    Usuario findByLogin(String login);

    Usuario findByEmail(String email);
}
