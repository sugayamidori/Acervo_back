package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.UsuarioDTO;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;

    public Optional<Usuario> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public void deletar(Usuario usuario) {
        repository.delete(usuario);
    }

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

    public Usuario obterPorNome(String nome) {
        return repository.findByNome(nome);
    }

    public Usuario obterPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Usuario> pesquisa(String nome, String role) {
        String nomeFiltro = nome;
        String[] roleFiltro;

        if(nome != null && role != null) {
            nomeFiltro = nome + "%";
            roleFiltro = new String[]{role.toUpperCase()};
            return repository.findByNomeAndRoles(nomeFiltro, roleFiltro);
        }

        if(nome != null) {
            return repository.findByNomeStartingWithIgnoreCase(nomeFiltro);
        }

        if (role != null) {
            roleFiltro = new String[]{role.toUpperCase()};
            return repository.findByRole(roleFiltro);
        }

        return null;
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
