package com.github.petervl80.acervoapi.repository;

import com.github.petervl80.acervoapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {

    Usuario findByNome(String nome);

    Usuario findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT(:nome, '%'))")
    List<Usuario> findByNomeStartingWithIgnoreCase(@Param("nome") String login);

    @Query(value = """
    SELECT * FROM usuario u
    WHERE u.roles && :roles
    """, nativeQuery = true)
    List<Usuario> findByRole(String[] roles);

    @Query(value = """
    SELECT * FROM usuario u
    WHERE u.nome ILIKE :nome
      AND u.roles && :roles
    """, nativeQuery = true)
    List<Usuario> findByNomeAndRoles(
            @Param("nome") String login,
            @Param("roles") String[] roles
    );
}
