package com.alerta360.service;

import com.alerta360.exception.sensor.SensorInativoException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.model.LeituraSensor;
import com.alerta360.model.Sensor;
import com.alerta360.repository.LeituraSensorRepository;
import com.alerta360.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeituraSensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private LeituraSensorRepository leituraRepository;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private LeituraSensorService leituraService;

    private Sensor sensorAtivo() {
        Sensor sensor = new Sensor();
        sensor.setCodigoSensor("TEMP001");
        sensor.setAtivo(true);
        return sensor;
    }

    private LeituraSensor novaLeitura(String status, Map<String, Object> dados) {
        LeituraSensor leitura = new LeituraSensor();
        leitura.setStatus(status);
        leitura.setDadosMap(dados);
        return leitura;
    }

    @Test
    void deveSalvarLeituraArredondandoValoresEAtualizandoSensor() {
        Sensor sensor = sensorAtivo();
        when(sensorRepository.findByCodigoSensor("TEMP001")).thenReturn(Optional.of(sensor));
        when(leituraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeituraSensor salva = leituraService.processarLeituraExistente("TEMP001",
                novaLeitura("OK", Map.of("temperatura", 25.4567)));

        assertThat(salva.getDadosMap()).containsEntry("temperatura", 25.46);
        assertThat(salva.getTimestamp()).isNotNull();
        assertThat(sensor.getUltimaLeitura()).isEqualTo(salva.getTimestamp());
        verify(alertaService, never()).verificarECriarAlertas(any());
    }

    @Test
    void deveAcionarVerificacaoDeAlertasQuandoStatusForAlerta() {
        when(sensorRepository.findByCodigoSensor("TEMP001")).thenReturn(Optional.of(sensorAtivo()));
        when(leituraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeituraSensor salva = leituraService.processarLeituraExistente("TEMP001",
                novaLeitura("ALERTA", Map.of("temperatura", 50.0)));

        verify(alertaService).verificarECriarAlertas(salva);
    }

    @Test
    void deveRejeitarLeituraDeSensorInativo() {
        Sensor sensor = sensorAtivo();
        sensor.setAtivo(false);
        when(sensorRepository.findByCodigoSensor("TEMP001")).thenReturn(Optional.of(sensor));

        assertThatThrownBy(() -> leituraService.processarLeituraExistente("TEMP001", novaLeitura("OK", Map.of())))
                .isInstanceOf(SensorInativoException.class);
        verify(leituraRepository, never()).save(any());
    }

    @Test
    void deveRejeitarLeituraDeSensorInexistente() {
        when(sensorRepository.findByCodigoSensor("XPTO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leituraService.processarLeituraExistente("XPTO", novaLeitura("OK", Map.of())))
                .isInstanceOf(SensorNaoEncontradoException.class);
    }
}
