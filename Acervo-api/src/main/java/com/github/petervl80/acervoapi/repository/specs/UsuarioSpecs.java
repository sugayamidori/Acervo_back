package com.github.petervl80.acervoapi.repository.specs;

import com.github.petervl80.acervoapi.model.Usuario;
import org.springframework.data.jpa.domain.Specification;

public class UsuarioSpecs {

    public static Specification<Usuario> loginLike(String login) {
        return (root, query, cb) ->
                cb.like( cb.upper(root.get("login")), "%" + login.toUpperCase() + "%");
    }

    public static Specification<Usuario> roleLike(String role) {
        return (root, query, cb) -> cb.isTrue(
                cb.function("ARRAY_TO_STRING", String.class, root.get("roles"), cb.literal(","))
                        .in(cb.literal("%" + role.toUpperCase() + "%"))
        );
    }

}
