package com.alerta360.service;

import com.alerta360.exception.usuario.EmailJaExisteException;
import com.alerta360.exception.usuario.SenhaIncorretaException;
import com.alerta360.model.Usuario;
import com.alerta360.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    private Usuario usuario(String email, String senhaPura, boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senhaPura));
        usuario.setAtivo(ativo);
        return usuario;
    }

    @Test
    void deveCriptografarSenhaAoCriarUsuario() {
        Usuario novo = new Usuario();
        novo.setEmail("ana@alerta360.com");
        novo.setSenha("segredo123");
        when(usuarioRepository.existsByEmail("ana@alerta360.com")).thenReturn(false);
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario criado = usuarioService.criarUsuario(novo);

        assertThat(criado.getSenha()).isNotEqualTo("segredo123");
        assertThat(passwordEncoder.matches("segredo123", criado.getSenha())).isTrue();
        assertThat(criado.isAtivo()).isTrue();
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        Usuario novo = new Usuario();
        novo.setEmail("ana@alerta360.com");
        when(usuarioRepository.existsByEmail("ana@alerta360.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criarUsuario(novo))
                .isInstanceOf(EmailJaExisteException.class);
    }

    @Test
    void deveValidarCredenciaisDeUsuarioAtivo() {
        when(usuarioRepository.findByEmail("ana@alerta360.com"))
                .thenReturn(Optional.of(usuario("ana@alerta360.com", "segredo123", true)));

        assertThat(usuarioService.validarCredenciais("ana@alerta360.com", "segredo123")).isTrue();
        assertThat(usuarioService.validarCredenciais("ana@alerta360.com", "errada")).isFalse();
    }

    @Test
    void deveNegarLoginDeUsuarioInativo() {
        when(usuarioRepository.findByEmail("ana@alerta360.com"))
                .thenReturn(Optional.of(usuario("ana@alerta360.com", "segredo123", false)));

        assertThat(usuarioService.validarCredenciais("ana@alerta360.com", "segredo123")).isFalse();
    }

    @Test
    void deveExigirSenhaAtualCorretaParaAlterarSenha() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario("ana@alerta360.com", "segredo123", true)));

        assertThatThrownBy(() -> usuarioService.alterarSenha(1L, "errada", "nova"))
                .isInstanceOf(SenhaIncorretaException.class);
        verify(usuarioRepository, never()).save(any());
    }
}
