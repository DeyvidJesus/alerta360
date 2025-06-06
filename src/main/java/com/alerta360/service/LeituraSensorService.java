package com.alerta360.service;

import com.alerta360.exception.sensor.SensorInativoException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.model.LeituraSensor;
import com.alerta360.model.Sensor;
import com.alerta360.repository.LeituraSensorRepository;
import com.alerta360.repository.SensorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeituraSensorService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    @Autowired
    private AlertaService alertaService;

    public LeituraSensor processarLeituraExistente(String codigoSensor, LeituraSensor leitura) {
        Sensor sensor = sensorRepository.findByCodigoSensor(codigoSensor)
                .orElseThrow(() -> new SensorNaoEncontradoException("Sensor não encontrado: " + codigoSensor));

        if (!sensor.isAtivo()) {
            throw new SensorInativoException("Sensor está inativo: " + codigoSensor);
        }

        leitura.setSensor(sensor);

        Map<String, Object> dadosLimpos = validarELimparDados(leitura.getDadosMap());
        leitura.setDadosMap(dadosLimpos);

        leitura.setTimestamp(LocalDateTime.now());

        leitura.setStatus(leitura.getStatus());


        LeituraSensor leituraSalva = leituraRepository.save(leitura);

        sensor.setUltimaLeitura(leituraSalva.getTimestamp());
        sensorRepository.save(sensor);

        if ("ALERTA".equalsIgnoreCase(leituraSalva.getStatus()) || "ERRO".equalsIgnoreCase(leituraSalva.getStatus())) {
            alertaService.verificarECriarAlertas(leituraSalva);
        }

        return leituraSalva;
    }

    private Map<String, Object> validarELimparDados(Map<String, Object> dados) {
        Map<String, Object> dadosLimpos = new HashMap<>();

        if (dados == null) return dadosLimpos;

        for (Map.Entry<String, Object> entry : dados.entrySet()) {
            Object valor = entry.getValue();

            if (valor != null) {
                if (valor instanceof Double) {
                    valor = Math.round((Double) valor * 100.0) / 100.0;
                } else if (valor instanceof Float) {
                    valor = Math.round((Float) valor * 100.0) / 100.0;
                }
                dadosLimpos.put(entry.getKey(), valor);
            }
        }
        return dadosLimpos;
    }

    public List<Map<String, Object>> obterDadosFormatados(String codigoSensor, int limiteLeituras) {
        Sensor sensor = sensorRepository.findByCodigoSensor(codigoSensor)
                .orElseThrow(() -> new SensorNaoEncontradoException("Sensor não encontrado"));

        List<LeituraSensor> leituras = leituraRepository
                .findTopNBySensorOrderByTimestampDesc(sensor, limiteLeituras);

        return leituras.stream().map(this::formatarLeituraParaResposta).collect(Collectors.toList());
    }

    public List<Map<String, Object>> obterDadosPorPeriodo(String codigoSensor, LocalDateTime inicio, LocalDateTime fim) {
        Sensor sensor = sensorRepository.findByCodigoSensor(codigoSensor)
                .orElseThrow(() -> new SensorNaoEncontradoException("Sensor não encontrado"));

        List<LeituraSensor> leituras = leituraRepository.findBySensorAndTimestampBetween(sensor, inicio, fim);

        return leituras.stream().map(this::formatarLeituraParaResposta).collect(Collectors.toList());
    }

    private Map<String, Object> formatarLeituraParaResposta(LeituraSensor leitura) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("id", leitura.getId());
        resultado.put("codigoSensor", leitura.getSensor().getCodigoSensor());
        resultado.put("timestamp", leitura.getTimestamp());
        resultado.put("status", leitura.getStatus());
        resultado.put("dados", leitura.getDadosMap());
        return resultado;
    }

    public Map<String, Object> calcularEstatisticasPeriodo(String codigoSensor, LocalDateTime inicio, LocalDateTime fim) {
        List<Map<String, Object>> dados = obterDadosPorPeriodo(codigoSensor, inicio, fim);

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalLeituras", dados.size());
        estatisticas.put("periodo", Map.of("inicio", inicio, "fim", fim));

        if (!dados.isEmpty()) {
            // Calcular estatísticas para cada campo numérico
            calcularEstatisticasCampos(dados, estatisticas);
        }

        return estatisticas;
    }

    private void calcularEstatisticasCampos(List<Map<String, Object>> dados, Map<String, Object> estatisticas) {
        Map<String, List<Double>> valoresPorCampo = new HashMap<>();

        // Extrair todos os valores numéricos
        for (Map<String, Object> leitura : dados) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dadosLeitura = (Map<String, Object>) leitura.get("dados");

            for (Map.Entry<String, Object> entry : dadosLeitura.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    valoresPorCampo
                            .computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                            .add(((Number) entry.getValue()).doubleValue());
                }
            }
        }

        // Calcular estatísticas para cada campo
        Map<String, Map<String, Double>> estatisticasCampos = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : valoresPorCampo.entrySet()) {
            String campo = entry.getKey();
            List<Double> valores = entry.getValue();

            Map<String, Double> stats = new HashMap<>();
            stats.put("media", valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            stats.put("minimo", valores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
            stats.put("maximo", valores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));

            estatisticasCampos.put(campo, stats);
        }

        estatisticas.put("campos", estatisticasCampos);
    }
}
