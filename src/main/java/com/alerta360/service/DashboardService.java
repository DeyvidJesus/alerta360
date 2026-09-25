package com.alerta360.service;

import com.alerta360.model.Alerta;
import com.alerta360.model.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DashboardService {

    private final SensorService sensorService;

    private final LeituraSensorService leituraService;

    private final AlertaService alertaService;

    public Map<String, Object> obterResumoGeral() {
        Map<String, Object> resumo = new HashMap<>();

        // Contadores gerais
        List<Sensor> sensores = sensorService.listarTodos();
        List<Sensor> sensoresAtivos = sensorService.listarAtivos();
        List<Alerta> alertasAtivos = alertaService.listarAlertasAtivos();

        resumo.put("totalSensores", sensores.size());
        resumo.put("sensoresAtivos", sensoresAtivos.size());
        resumo.put("sensoresInativos", sensores.size() - sensoresAtivos.size());
        resumo.put("alertasAtivos", alertasAtivos.size());

        // Sensores por tipo
        Map<String, Long> sensoresPorTipo = sensores.stream()
                .collect(Collectors.groupingBy(Sensor::getTipo, Collectors.counting()));
        resumo.put("sensoresPorTipo", sensoresPorTipo);

        // Alertas por tipo
        Map<String, Long> alertasPorTipo = alertaService.contarAlertasPorTipo();
        resumo.put("alertasPorTipo", alertasPorTipo);

        return resumo;
    }

    public List<Map<String, Object>> obterUltimasLeiturasTodosSensores() {
        List<Sensor> sensores = sensorService.listarAtivos();
        List<Map<String, Object>> resumoSensores = new ArrayList<>();

        for (Sensor sensor : sensores) {
            try {
                List<Map<String, Object>> ultimasLeituras = leituraService
                        .obterDadosFormatados(sensor.getCodigoSensor(), 1);

                Map<String, Object> resumoSensor = new HashMap<>();
                resumoSensor.put("codigoSensor", sensor.getCodigoSensor());
                resumoSensor.put("nome", sensor.getNome());
                resumoSensor.put("tipo", sensor.getTipo());
                resumoSensor.put("localizacao", sensor.getLocalizacao());

                if (!ultimasLeituras.isEmpty()) {
                    Map<String, Object> ultimaLeitura = ultimasLeituras.get(0);
                    resumoSensor.put("ultimaLeitura", ultimaLeitura.get("timestamp"));
                    resumoSensor.put("status", ultimaLeitura.get("status"));
                    resumoSensor.put("dados", ultimaLeitura.get("dados"));
                } else {
                    resumoSensor.put("status", "SEM_DADOS");
                }

                resumoSensores.add(resumoSensor);

            } catch (Exception e) {
                // Falha em um sensor não deve derrubar o dashboard inteiro
                log.warn("Erro ao obter dados do sensor {}: {}", sensor.getCodigoSensor(), e.getMessage());
            }
        }

        return resumoSensores;
    }
}