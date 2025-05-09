package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public void salvar(Usuario usuario) {
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        repository.save(usuario);
    }

    public Usuario obterPorLogin(String login) {
        return repository.findByLogin(login);
    }

    public Usuario obterPorEmail(String email) {
        return repository.findByEmail(email);
    }
}
