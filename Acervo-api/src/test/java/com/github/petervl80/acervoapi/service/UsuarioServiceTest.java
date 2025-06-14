package com.github.petervl80.acervoapi.service;

import com.github.petervl80.acervoapi.controller.dto.LoginUsuarioDTO;
import com.github.petervl80.acervoapi.model.Usuario;
import com.github.petervl80.acervoapi.repository.UsuarioRepository;
import com.github.petervl80.acervoapi.validator.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UsuarioValidator validator;

    @InjectMocks
    private UsuarioService service;

    private Usuario membro;
    private Usuario bibliotecario;
    private Usuario admin;

    @BeforeEach
    void setup() {
        membro = new Usuario();
        membro.setId(UUID.randomUUID());
        membro.setNome("membro");
        membro.setEmail("membro@gmail.com");
        membro.setSenha("membro123");
        membro.setRoles(List.of("MEMBRO"));

        bibliotecario = new Usuario();
        bibliotecario.setId(UUID.randomUUID());
        bibliotecario.setNome("bibliotecario");
        bibliotecario.setEmail("bibliotecario@gmail.com");
        bibliotecario.setSenha("bibliotecario123");
        bibliotecario.setRoles(List.of("BIBLIOTECARIO"));

        admin = new Usuario();
        admin.setId(UUID.randomUUID());
        admin.setNome("admin");
        admin.setEmail("admin@gmail.com");
        admin.setSenha("admin123");
        admin.setRoles(List.of("ADMINISTRADOR"));
    }

    @Test
    void deveCadastrarMembro() {
        membro.setRoles(null); // Anular a role para teste
        String senha = membro.getSenha();
        String senhaCriptografada = "senha-criptografada";

        when(encoder.encode(senha)).thenReturn(senhaCriptografada);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = service.salvarMembro(membro);

        verify(validator).validar(membro);
        verify(encoder).encode(senha);
        verify(repository).save(membro);

        assertEquals(senhaCriptografada, usuarioSalvo.getSenha());
        assertEquals(List.of("MEMBRO"), usuarioSalvo.getRoles());
    }

    @Test
    void deveCadastrarUsuarioMembro() {
        String senha = membro.getSenha();
        String senhaCriptografada = "senha-criptografada";

        when(encoder.encode(senha)).thenReturn(senhaCriptografada);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = service.salvarUsuario(membro);

        verify(validator).validar(membro);
        verify(encoder).encode(senha);
        verify(repository).save(membro);

        assertEquals(senhaCriptografada, usuarioSalvo.getSenha());
        assertEquals(List.of("MEMBRO"), usuarioSalvo.getRoles());
    }

    @Test
    void deveCadastrarUsuarioBibliotecario() {
        String senha = bibliotecario.getSenha();
        String senhaCriptografada = "senha-criptografada";

        when(encoder.encode(senha)).thenReturn(senhaCriptografada);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = service.salvarUsuario(bibliotecario);

        verify(validator).validar(bibliotecario);
        verify(encoder).encode(senha);
        verify(repository).save(bibliotecario);

        assertEquals(senhaCriptografada, usuarioSalvo.getSenha());
        assertEquals(List.of("BIBLIOTECARIO"), usuarioSalvo.getRoles());
    }

    @Test
    void deveCadastrarUsuarioAdministrador() {
        String senha = admin.getSenha();
        String senhaCriptografada = "senha-criptografada";

        when(encoder.encode(senha)).thenReturn(senhaCriptografada);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = service.salvarUsuario(admin);

        verify(validator).validar(admin);
        verify(encoder).encode(senha);
        verify(repository).save(admin);

        assertEquals(senhaCriptografada, usuarioSalvo.getSenha());
        assertEquals(List.of("ADMINISTRADOR"), usuarioSalvo.getRoles());
    }

    @Test
    void deveObterUsuarioPorNome() {
        String nome = bibliotecario.getNome();
        when(repository.findByNome(nome)).thenReturn(bibliotecario);

        Usuario encontrado = service.obterPorNome(nome);

        verify(repository).findByNome(nome);

        assertEquals(bibliotecario, encontrado);
    }

    @Test
    void deveObterUsuarioPorEmail() {
        String email = admin.getEmail();
        when(repository.findByEmail(email)).thenReturn(admin);

        Usuario encontrado = service.obterPorEmail(email);

        verify(repository).findByEmail(email);

        assertEquals(admin, encontrado);
    }

    @ParameterizedTest
    @CsvSource({
            "membro, MEMBRO",
            "bibliotecario, BIBLIOTECARIO",
            "admin, ADMINISTRADOR"
    })
    void devePesquisarPorNomeERoleDinamico(String nome, String role) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setRoles(List.of(role));

        when(repository.findAll(any(Specification.class)))
                .thenReturn(List.of(usuario));

        List<Usuario> resultado = service.pesquisa(nome, role);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        Usuario resultadoUsuario = resultado.getFirst();
        assertEquals(nome, resultadoUsuario.getNome());
        assertTrue(resultadoUsuario.getRoles().contains(role));

        verify(repository).findAll(any(Specification.class));
    }

    @Test
    void deveAutenticarUsuarioComSucesso() {
        String senhaCriptografada = "senha-criptografada";
        membro.setSenha(senhaCriptografada);

        when(repository.findByEmail(membro.getEmail())).thenReturn(membro);
        when(encoder.matches("membro123", senhaCriptografada)).thenReturn(true);

        Usuario autenticado = service.autenticar(new LoginUsuarioDTO(membro.getEmail(),
                "membro123"));

        assertEquals(membro, autenticado);
        verify(repository).findByEmail(membro.getEmail());
        verify(encoder).matches("membro123", senhaCriptografada);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoNaAutenticacao() {
        LoginUsuarioDTO dto = new LoginUsuarioDTO("inexistente@gmail.com","senha123");

        when(repository.findByEmail(dto.email())).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.autenticar(dto)
        );

        assertEquals("Usuário e/ou senha incorretos", exception.getMessage());

        verify(repository).findByEmail(dto.email());
        verifyNoInteractions(encoder);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorretaNaAutenticacao() {
        LoginUsuarioDTO dto = new LoginUsuarioDTO(membro.getEmail(),
                "membro1234");
        membro.setSenha("senha-encriptografada");

        when(repository.findByEmail(dto.email())).thenReturn(membro);
        when(encoder.matches(dto.senha(), membro.getSenha())).thenReturn(false);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.autenticar(dto)
        );

        assertEquals("Usuário e/ou senha incorretos", exception.getMessage());

        verify(repository).findByEmail(dto.email());
        verify(encoder).matches(dto.senha(), membro.getSenha());
    }
}