package com.alerta360.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegrasAlertaProvider {

    public List<RegraAlerta> getRegras() {
        return List.of(
                new RegraAlerta("temperatura", 45, ">", "TEMPERATURA_ALTA", "Temperatura crítica: {valor}°C"),
                new RegraAlerta("temperatura", -10, "<", "TEMPERATURA_BAIXA", "Temperatura muito baixa: {valor}°C"),
                new RegraAlerta("umidade", 20, "<", "UMIDADE_BAIXA", "Umidade abaixo do limite: {valor}%"),
                new RegraAlerta("co2", 1000, ">", "CO2_ALTO", "Nível de CO2 elevado: {valor} ppm"),
                new RegraAlerta("gas", 1, ">", "GAS_DETECTADO", "Presença de gás detectada: {valor}")
        );
    }
}
