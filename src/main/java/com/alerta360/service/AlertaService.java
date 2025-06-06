package com.alerta360.service;

import com.alerta360.exception.alerta.AlertaNaoEncontradoException;
import com.alerta360.exception.sensor.SensorNaoEncontradoException;
import com.alerta360.model.Alerta;
import com.alerta360.model.LeituraSensor;
import com.alerta360.model.Sensor;
import com.alerta360.repository.LeituraSensorRepository;
import com.alerta360.repository.AlertaRepository;
import com.alerta360.repository.SensorRepository;
import com.alerta360.utils.RegraAlerta;
import com.alerta360.utils.RegrasAlertaProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private LeituraSensorRepository leituraRepository;

    @Autowired
    private RegrasAlertaProvider regrasProvider;

    @Autowired
    private SensorRepository sensorRepository;

    public void verificarECriarAlertas(LeituraSensor leitura) {
        String status = leitura.getStatus();
        Map<String, Object> dados = leitura.getDadosMap();

        if ("ALERTA".equals(status)) {
            criarAlertasEspecificos(leitura, dados);
        } else if ("ERRO".equals(status)) {
            criarAlerta(leitura, "ERRO_LEITURA", "Erro na leitura dos dados do sensor");
        }
    }

    private void criarAlertasEspecificos(LeituraSensor leitura, Map<String, Object> dados) {
        for (RegraAlerta regra : regrasProvider.getRegras()) {
            if (!dados.containsKey(regra.getCampo())) continue;

            try {
                Double valor = Double.parseDouble(dados.get(regra.getCampo()).toString());

                if (regra.verificar(valor)) {
                    criarAlerta(leitura, regra.getTipoAlerta(), regra.getMensagemFormatada(valor));
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void criarAlerta(LeituraSensor leitura, String tipoAlerta, String mensagem) {
        Sensor sensor = leitura.getSensor();
        LocalDateTime limite = LocalDateTime.now().minusHours(2);

        boolean alertaRecente = alertaRepository.existsByTipoAlertaAndSensorAndDataHoraAfterAndResolvidoFalse(
                tipoAlerta, sensor, limite
        );

        if (!alertaRecente) {
            Alerta alerta = new Alerta();
            alerta.setSensor(sensor);
            alerta.setLeitura(leitura);
            alerta.setTipoAlerta(tipoAlerta);
            alerta.setMensagem(mensagem);
            alerta.setDataHora(LocalDateTime.now());
            alerta.setResolvido(false);

            alertaRepository.save(alerta);
        }
    }

    public List<Alerta> listarAlertasAtivos() {
        return alertaRepository.findByResolvidoFalseOrderByDataHoraDesc();
    }

    public List<Alerta> listarAlertasPorSensor(String codigoSensor) {
        return alertaRepository.findBySensorCodigoSensorOrderByDataHoraDesc(codigoSensor);
    }

    public Alerta resolverAlerta(Long alertaId, String observacoes) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new AlertaNaoEncontradoException("Alerta não encontrado"));

        alerta.setResolvido(true);
        alerta.setDataResolucao(LocalDateTime.now());

        return alertaRepository.save(alerta);
    }

    public Map<String, Long> contarAlertasPorTipo() {
        List<Alerta> alertasAtivos = listarAlertasAtivos();

        return alertasAtivos.stream()
                .collect(Collectors.groupingBy(
                        Alerta::getTipoAlerta,
                        Collectors.counting()
                ));
    }

    public void verificarSensoresOffline() {
        LocalDateTime limite = LocalDateTime.now().minusHours(1);

        List<LeituraSensor> ultimasLeituras = leituraRepository.findUltimaLeituraPorSensor();

        for (LeituraSensor ultimaLeitura : ultimasLeituras) {
            if (ultimaLeitura.getTimestamp().isBefore(limite)) {
                boolean alertaExiste = alertaRepository.existsByTipoAlertaAndSensorAndResolvidoFalse(
                        "SENSOR_OFFLINE", ultimaLeitura.getSensor());

                if (!alertaExiste) {
                    criarAlerta(ultimaLeitura, "SENSOR_OFFLINE",
            "Sensor não envia dados há mais de 1 hora");
                }
            }
        }
    }

    public Alerta criarAlertaManual(Alerta alertaJson) {
        if (alertaJson.getSensor() == null || alertaJson.getSensor().getId() == null) {
            throw new IllegalArgumentException("Sensor deve ser informado com ID");
        }

        Sensor sensor = sensorRepository.findById(alertaJson.getSensor().getId())
                .orElseThrow(() -> new SensorNaoEncontradoException("Sensor não encontrado"));

        alertaJson.setSensor(sensor);

        if (alertaJson.getLeitura() != null && alertaJson.getLeitura().getId() != null) {
            LeituraSensor leitura = leituraRepository.findById(alertaJson.getLeitura().getId())
                    .orElseThrow(() -> new RuntimeException("Leitura não encontrada"));
            alertaJson.setLeitura(leitura);
        } else {
            alertaJson.setLeitura(null);
        }

        if (alertaJson.getDataHora() == null) {
            alertaJson.setDataHora(LocalDateTime.now());
        }

        alertaJson.setResolvido(false);
        alertaJson.setDataResolucao(null);

        return alertaRepository.save(alertaJson);
    }
}