package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.UsuarioDTO;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.petervl80.acervoapi.repository.specs.UsuarioSpecs.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;

    public Usuario salvarMembro(Usuario usuario) {
        validator.validar(usuario);
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuario.setRoles(List.of("MEMBRO"));
        return repository.save(usuario);
    }

    public Usuario salvarUsuario(Usuario usuario) {
        validator.validar(usuario);
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        return repository.save(usuario);
    }

    public Usuario obterPorLogin(String login) {
        return repository.findByLogin(login);
    }

    public Usuario obterPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Usuario> pesquisa(String login, String role) {

        Specification<Usuario> specs = (root, query, cb) -> cb.conjunction();

        if(login != null) {
            specs = specs.and(loginLike(login));
        }

        if(role != null) {
            specs = specs.and(roleLike(role));
        }

        return repository.findAll(specs);
    }

    public Usuario autenticar(UsuarioDTO dto) {
        String email = dto.email();
        String senhaDigitada = dto.senha();

        Usuario usuarioEncontrado = obterPorEmail(email);

        if (usuarioEncontrado == null) {
            throw getErroUsuarioNaoEncontrado();
        }

        String senhaCriptografada = usuarioEncontrado.getSenha();

        boolean senhasBatem = encoder.matches(senhaDigitada, senhaCriptografada);

        if (senhasBatem) {
            return usuarioEncontrado;
        }

        throw getErroUsuarioNaoEncontrado();
    }

    private static UsernameNotFoundException getErroUsuarioNaoEncontrado() {
        return new UsernameNotFoundException("Usuário e/ou senha incorretos");
    }
}
