package com.alerta360.service;

import com.alerta360.exception.alerta.AlertaJaResolvidoException;
import com.alerta360.exception.alerta.AlertaNaoEncontradoException;
import com.alerta360.model.Alerta;
import com.alerta360.model.LeituraSensor;
import com.alerta360.model.Sensor;
import com.alerta360.repository.AlertaRepository;
import com.alerta360.repository.LeituraSensorRepository;
import com.alerta360.repository.SensorRepository;
import com.alerta360.utils.RegrasAlertaProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private LeituraSensorRepository leituraRepository;

    @Mock
    private SensorRepository sensorRepository;

    private AlertaService alertaService;

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        // Usa as regras reais para validar os thresholds configurados
        alertaService = new AlertaService(alertaRepository, leituraRepository, new RegrasAlertaProvider(), sensorRepository);

        sensor = new Sensor();
        sensor.setId(1L);
        sensor.setCodigoSensor("TEMP001");
    }

    private LeituraSensor leitura(String status, Map<String, Object> dados) {
        LeituraSensor leitura = new LeituraSensor();
        leitura.setSensor(sensor);
        leitura.setStatus(status);
        leitura.setDadosMap(dados);
        leitura.setTimestamp(LocalDateTime.now());
        return leitura;
    }

    @Test
    void deveCriarAlertaQuandoValorUltrapassaLimite() {
        when(alertaRepository.existsByTipoAlertaAndSensorAndDataHoraAfterAndResolvidoFalse(anyString(), any(), any()))
                .thenReturn(false);

        alertaService.verificarECriarAlertas(leitura("ALERTA", Map.of("temperatura", 50.0)));

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipoAlerta()).isEqualTo("TEMPERATURA_ALTA");
        assertThat(captor.getValue().getMensagem()).isEqualTo("Temperatura crítica: 50.0°C");
        assertThat(captor.getValue().isResolvido()).isFalse();
    }

    @Test
    void naoDeveCriarAlertaQuandoValorEstaDentroDoLimite() {
        alertaService.verificarECriarAlertas(leitura("ALERTA", Map.of("temperatura", 25.0, "umidade", 60.0)));

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void naoDeveDuplicarAlertaDentroDaJanelaDeThrottling() {
        when(alertaRepository.existsByTipoAlertaAndSensorAndDataHoraAfterAndResolvidoFalse(
                eq("TEMPERATURA_ALTA"), eq(sensor), any())).thenReturn(true);

        alertaService.verificarECriarAlertas(leitura("ALERTA", Map.of("temperatura", 50.0)));

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void deveCriarAlertaDeErroQuandoLeituraTemStatusErro() {
        alertaService.verificarECriarAlertas(leitura("ERRO", Map.of()));

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipoAlerta()).isEqualTo("ERRO_LEITURA");
    }

    @Test
    void deveIgnorarCamposNaoNumericos() {
        alertaService.verificarECriarAlertas(leitura("ALERTA", Map.of("temperatura", "sem-sinal")));

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void deveCriarAlertaOfflineParaSensorSemLeituraRecente() {
        LeituraSensor antiga = leitura("OK", Map.of());
        antiga.setTimestamp(LocalDateTime.now().minusHours(3));
        when(leituraRepository.findUltimaLeituraPorSensor()).thenReturn(List.of(antiga));
        when(alertaRepository.existsByTipoAlertaAndSensorAndResolvidoFalse("SENSOR_OFFLINE", sensor)).thenReturn(false);

        alertaService.verificarSensoresOffline();

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipoAlerta()).isEqualTo("SENSOR_OFFLINE");
    }

    @Test
    void deveResolverAlertaAtivo() {
        Alerta alerta = new Alerta();
        alerta.setId(10L);
        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alerta));
        when(alertaRepository.save(alerta)).thenReturn(alerta);

        Alerta resolvido = alertaService.resolverAlerta(10L, null);

        assertThat(resolvido.isResolvido()).isTrue();
        assertThat(resolvido.getDataResolucao()).isNotNull();
    }

    @Test
    void deveRejeitarResolucaoDeAlertaJaResolvido() {
        Alerta alerta = new Alerta();
        alerta.setResolvido(true);
        when(alertaRepository.findById(10L)).thenReturn(Optional.of(alerta));

        assertThatThrownBy(() -> alertaService.resolverAlerta(10L, null))
                .isInstanceOf(AlertaJaResolvidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoAlertaNaoExiste() {
        when(alertaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertaService.resolverAlerta(99L, null))
                .isInstanceOf(AlertaNaoEncontradoException.class);
    }
}
