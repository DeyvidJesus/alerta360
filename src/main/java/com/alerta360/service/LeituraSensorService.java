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

    public LeituraSensor processarDadosSensor(String codigoSensor, Map<String, Object> dadosJson) {
        // 1. Buscar sensor
        Sensor sensor = sensorRepository.findByCodigoSensor(codigoSensor)
                .orElseThrow(() -> new SensorNaoEncontradoException("Sensor não encontrado: " + codigoSensor));

        if (!sensor.isAtivo()) {
            throw new SensorInativoException("Sensor está inativo: " + codigoSensor);
        }

        // 2. Validar e limpar dados
        Map<String, Object> dadosLimpos = validarELimparDados(dadosJson);

        // 3. Criar leitura
        LeituraSensor leitura = new LeituraSensor();
        leitura.setSensor(sensor);
        leitura.setDadosMap(dadosLimpos);
        leitura.setTimestamp(LocalDateTime.now());

        // 4. Analisar dados e definir status
        String status = analisarDados(dadosLimpos, sensor.getTipo());
        leitura.setStatus(status);

        // 5. Salvar leitura
        leitura = leituraRepository.save(leitura);

        // 6. Atualizar última leitura do sensor
        sensor.setUltimaLeitura(leitura.getTimestamp());
        sensorRepository.save(sensor);

        // 7. Verificar se precisa gerar alertas
        if ("ALERTA".equals(status) || "ERRO".equals(status)) {
            alertaService.verificarECriarAlertas(leitura);
        }

        return leitura;
    }

    private Map<String, Object> validarELimparDados(Map<String, Object> dados) {
        Map<String, Object> dadosLimpos = new HashMap<>();

        for (Map.Entry<String, Object> entry : dados.entrySet()) {
            String chave = entry.getKey();
            Object valor = entry.getValue();

            // Limpar valores nulos
            if (valor != null) {
                // Arredondar números decimais para 2 casas
                if (valor instanceof Double) {
                    valor = Math.round((Double) valor * 100.0) / 100.0;
                } else if (valor instanceof Float) {
                    valor = Math.round((Float) valor * 100.0) / 100.0;
                }

                dadosLimpos.put(chave, valor);
            }
        }

        return dadosLimpos;
    }

    private String analisarDados(Map<String, Object> dados, String tipoSensor) {
        try {
            // Verificar bateria baixa
            if (dados.containsKey("bateria")) {
                Double bateria = getDoubleValue(dados.get("bateria"));
                if (bateria != null && bateria < 15.0) {
                    return "ALERTA";
                }
            }

            // Verificar sinal fraco
            if (dados.containsKey("sinalRSSI")) {
                Integer rssi = getIntegerValue(dados.get("sinalRSSI"));
                if (rssi != null && rssi < -80) {
                    return "ALERTA";
                }
            }

            // Análises específicas por tipo de sensor
            return analisarPorTipoSensor(dados, tipoSensor);

        } catch (Exception e) {
            return "ERRO";
        }
    }

    private String analisarPorTipoSensor(Map<String, Object> dados, String tipoSensor) {
        switch (tipoSensor.toUpperCase()) {
            case "TEMPERATURA":
                return analisarTemperatura(dados);
            case "UMIDADE":
                return analisarUmidade(dados);
            case "QUALIDADE_AR":
                return analisarQualidadeAr(dados);
            case "MULTISENSOR":
                return analisarMultisensor(dados);
            default:
                return "OK";
        }
    }

    private String analisarTemperatura(Map<String, Object> dados) {
        Double temp = getDoubleValue(dados.get("temperatura"));
        if (temp != null) {
            if (temp < -20 || temp > 60) {
                return "ALERTA";
            }
        }
        return "OK";
    }

    private String analisarUmidade(Map<String, Object> dados) {
        Double umidade = getDoubleValue(dados.get("umidade"));
        if (umidade != null) {
            if (umidade < 0 || umidade > 100) {
                return "ERRO";
            }
            if (umidade < 20 || umidade > 80) {
                return "ALERTA";
            }
        }
        return "OK";
    }

    private String analisarQualidadeAr(Map<String, Object> dados) {
        Double co2 = getDoubleValue(dados.get("co2"));
        if (co2 != null && co2 > 1000) {
            return "ALERTA";
        }

        Double pm25 = getDoubleValue(dados.get("pm25"));
        if (pm25 != null && pm25 > 25) {
            return "ALERTA";
        }

        return "OK";
    }

    private String analisarMultisensor(Map<String, Object> dados) {
        // Verifica múltiplos parâmetros
        String statusTemp = analisarTemperatura(dados);
        String statusUmid = analisarUmidade(dados);

        if ("ERRO".equals(statusTemp) || "ERRO".equals(statusUmid)) {
            return "ERRO";
        }
        if ("ALERTA".equals(statusTemp) || "ALERTA".equals(statusUmid)) {
            return "ALERTA";
        }

        return "OK";
    }

    // Métodos utilitários para conversão segura
    private Double getDoubleValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private Integer getIntegerValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
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