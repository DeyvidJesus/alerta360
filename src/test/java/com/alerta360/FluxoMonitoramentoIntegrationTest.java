package com.alerta360;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercita o fluxo principal de ponta a ponta (HTTP -> service -> JPA), usando H2 em memória:
 * cadastro de sensor, envio de leitura acima do limite e geração automática do alerta.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoMonitoramentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void leituraAcimaDoLimiteDeveGerarAlertaAtivo() throws Exception {
        mockMvc.perform(post("/api/sensores").contentType(MediaType.APPLICATION_JSON).content("""
                        {"codigoSensor": "INT001", "nome": "Câmara fria", "tipo": "temperatura", "localizacao": "Galpão A"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/leituras/INT001").contentType(MediaType.APPLICATION_JSON).content("""
                        {"status": "ALERTA", "dadosMap": {"temperatura": 52.3, "umidade": 40}}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/alertas/sensor/INT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tipoAlerta", hasItem("TEMPERATURA_ALTA")))
                .andExpect(jsonPath("$[0].resolvido").value(false));

        mockMvc.perform(get("/api/dashboard/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertasAtivos").value(1));
    }

    @Test
    void documentacaoOpenApiDeveEstarDisponivel() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Alerta360 API"))
                .andExpect(jsonPath("$.paths['/api/sensores']").exists());
    }

    @Test
    void leituraDeSensorInexistenteDeveRetornar404() throws Exception {
        mockMvc.perform(post("/api/leituras/NAO_EXISTE").contentType(MediaType.APPLICATION_JSON).content("""
                        {"status": "OK", "dadosMap": {"temperatura": 20}}
                        """))
                .andExpect(status().isNotFound());
    }
}
