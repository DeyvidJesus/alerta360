package com.alerta360.service;

import com.alerta360.exception.sensor.SensorJaExisteException;
import com.alerta360.model.Sensor;
import com.alerta360.repository.LeituraSensorRepository;
import com.alerta360.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private LeituraSensorRepository leituraRepository;

    @InjectMocks
    private SensorService sensorService;

    @Test
    void deveCriarSensorAtivoComDataDeCadastro() {
        Sensor sensor = new Sensor();
        sensor.setCodigoSensor("TEMP001");
        when(sensorRepository.existsByCodigoSensor("TEMP001")).thenReturn(false);
        when(sensorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sensor criado = sensorService.criarSensor(sensor);

        assertThat(criado.isAtivo()).isTrue();
        assertThat(criado.getDataCadastro()).isNotNull();
    }

    @Test
    void deveRejeitarCodigoDuplicado() {
        Sensor sensor = new Sensor();
        sensor.setCodigoSensor("TEMP001");
        when(sensorRepository.existsByCodigoSensor("TEMP001")).thenReturn(true);

        assertThatThrownBy(() -> sensorService.criarSensor(sensor))
                .isInstanceOf(SensorJaExisteException.class);
        verify(sensorRepository, never()).save(any());
    }

    @Test
    void deveAtualizarApenasCamposInformados() {
        Sensor existente = new Sensor();
        existente.setCodigoSensor("TEMP001");
        existente.setNome("Nome antigo");
        existente.setLocalizacao("Sala 1");
        when(sensorRepository.findByCodigoSensor("TEMP001")).thenReturn(Optional.of(existente));
        when(sensorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sensor dados = new Sensor();
        dados.setNome("Nome novo");
        Sensor atualizado = sensorService.atualizarSensor("TEMP001", dados);

        assertThat(atualizado.getNome()).isEqualTo("Nome novo");
        assertThat(atualizado.getLocalizacao()).isEqualTo("Sala 1");
    }
}
