package com.alerta360.controller;

import com.alerta360.config.SecurityConfiguration;
import com.alerta360.exception.sensor.SensorJaExisteException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.model.Sensor;
import com.alerta360.service.SensorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
@Import(SecurityConfiguration.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SensorService sensorService;

    private static final String SENSOR_JSON = """
            {"codigoSensor": "TEMP001", "nome": "Sala 1", "tipo": "temperatura", "localizacao": "Andar 2"}
            """;

    @Test
    void deveRetornar201AoCriarSensor() throws Exception {
        Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setCodigoSensor("TEMP001");
        sensor.setAtivo(true);
        when(sensorService.criarSensor(any())).thenReturn(sensor);

        mockMvc.perform(post("/api/sensores").contentType(MediaType.APPLICATION_JSON).content(SENSOR_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoSensor").value("TEMP001"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar409QuandoCodigoJaExiste() throws Exception {
        when(sensorService.criarSensor(any())).thenThrow(new SensorJaExisteException("Sensor com código TEMP001 já existe"));

        mockMvc.perform(post("/api/sensores").contentType(MediaType.APPLICATION_JSON).content(SENSOR_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Sensor com código TEMP001 já existe"));
    }

    @Test
    void deveRetornar404QuandoSensorNaoExiste() throws Exception {
        when(sensorService.buscarPorCodigo("XPTO")).thenThrow(new SensorNaoEncontradoException("Sensor não encontrado: XPTO"));

        mockMvc.perform(get("/api/sensores/XPTO"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400ParaJsonMalformado() throws Exception {
        mockMvc.perform(post("/api/sensores").contentType(MediaType.APPLICATION_JSON).content("{invalido"))
                .andExpect(status().isBadRequest());
    }
}
