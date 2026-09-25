package com.alerta360.controller;

import com.alerta360.config.SecurityConfiguration;
import com.alerta360.model.TipoUsuario;
import com.alerta360.model.Usuario;
import com.alerta360.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfiguration.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void naoDeveExporSenhaNaResposta() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("ana@alerta360.com");
        usuario.setSenha("$2a$10$hashbcrypt");
        usuario.setTipo(TipoUsuario.OPERADOR);
        when(usuarioService.criarUsuario(any())).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email": "ana@alerta360.com", "nome": "Ana", "senha": "segredo123", "tipo": "OPERADOR"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@alerta360.com"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
        when(usuarioService.validarCredenciais("ana@alerta360.com", "errada")).thenReturn(false);

        mockMvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email": "ana@alerta360.com", "senha": "errada"}
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.sucesso").value(false));
    }
}
