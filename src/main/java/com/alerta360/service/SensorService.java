package com.alerta360.service;

import com.alerta360.exception.sensor.SensorJaExisteException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.model.LeituraSensor;
import com.alerta360.model.Sensor;
import com.alerta360.repository.LeituraSensorRepository;
import com.alerta360.repository.SensorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    public Sensor criarSensor(Sensor sensor) {
        // Verificar se código já existe
        if (sensorRepository.existsByCodigoSensor(sensor.getCodigoSensor())) {
            throw new SensorJaExisteException("Sensor com código " + sensor.getCodigoSensor() + " já existe");
        }

        sensor.setDataCadastro(LocalDateTime.now());
        sensor.setAtivo(true);

        return sensorRepository.save(sensor);
    }

    public Sensor buscarPorCodigo(String codigoSensor) {
        return sensorRepository.findByCodigoSensor(codigoSensor)
                .orElseThrow(() -> new SensorNaoEncontradoException("Sensor não encontrado: " + codigoSensor));
    }

    public List<Sensor> listarTodos() {
        return sensorRepository.findAll();
    }

    public List<Sensor> listarAtivos() {
        return sensorRepository.findByAtivoTrue();
    }

    public List<Sensor> listarPorTipo(String tipo) {
        return sensorRepository.findByTipo(tipo);
    }

    public Sensor atualizarSensor(String codigoSensor, Sensor dadosAtualizacao) {
        Sensor sensor = buscarPorCodigo(codigoSensor);

        if (dadosAtualizacao.getNome() != null) {
            sensor.setNome(dadosAtualizacao.getNome());
        }
        if (dadosAtualizacao.getTipo() != null) {
            sensor.setTipo(dadosAtualizacao.getTipo());
        }
        if (dadosAtualizacao.getLocalizacao() != null) {
            sensor.setLocalizacao(dadosAtualizacao.getLocalizacao());
        }

        return sensorRepository.save(sensor);
    }

    public void ativarDesativarSensor(String codigoSensor, boolean ativo) {
        Sensor sensor = buscarPorCodigo(codigoSensor);
        sensor.setAtivo(ativo);
        sensorRepository.save(sensor);
    }

    public void deletarSensor(String codigoSensor) {
        Sensor sensor = buscarPorCodigo(codigoSensor);
        sensorRepository.delete(sensor);
    }

    public Map<String, Object> obterEstatisticasSensor(String codigoSensor) {
        Sensor sensor = buscarPorCodigo(codigoSensor);

        long totalLeituras = leituraRepository.countBySensor(sensor);
        Optional<LeituraSensor> ultimaLeitura = leituraRepository.findTopBySensorOrderByTimestampDesc(sensor);

        Map<String, Object> stats = new HashMap<>();
        stats.put("codigoSensor", sensor.getCodigoSensor());
        stats.put("nome", sensor.getNome());
        stats.put("tipo", sensor.getTipo());
        stats.put("ativo", sensor.isAtivo());
        stats.put("totalLeituras", totalLeituras);
        stats.put("dataCadastro", sensor.getDataCadastro());
        stats.put("ultimaLeitura", sensor.getUltimaLeitura());

        if (ultimaLeitura.isPresent()) {
            stats.put("ultimosDados", ultimaLeitura.get().getDadosMap());
            stats.put("ultimoStatus", ultimaLeitura.get().getStatus());
        }

        return stats;
    }

    public List<Sensor> buscarSensoresInativos(int horasSemLeitura) {
        LocalDateTime limite = LocalDateTime.now().minusHours(horasSemLeitura);
        return sensorRepository.findSensoresSemLeituraRecente(limite);
    }
}