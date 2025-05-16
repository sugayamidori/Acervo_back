package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.petervl80.acervoapi.repository.specs.UsuarioSpecs.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public void salvarMembro(Usuario usuario) {
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuario.setRoles(List.of("MEMBRO"));
        repository.save(usuario);
    }

    public void salvarAdministrador(Usuario usuario) {
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuario.setRoles(List.of("ADMINISTRADOR"));
        repository.save(usuario);
    }

    public void salvarBibliotecario(Usuario usuario) {
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuario.setRoles(List.of("BIBLIOTECARIO"));
        repository.save(usuario);
    }

    public Usuario obterPorLogin(String login) {
        return repository.findByLogin(login);
    }

    public Usuario obterPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public Page<Usuario> pesquisa(String nome, String role, Integer pagina, Integer tamanhoPagina) {

        Specification<Usuario> specs = (root, query, cb) -> cb.conjunction();

        if(nome != null) {
            specs = specs.and(nomeLike(nome));
        }

        if(role != null) {
            specs.and(roleLike(role));
        }
        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);

        return repository.findAll(specs, pageRequest);
    }
}
