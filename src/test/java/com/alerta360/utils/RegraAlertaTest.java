package com.alerta360.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegraAlertaTest {

    @ParameterizedTest(name = "{1} {0} 30 -> {2}")
    @CsvSource({
            ">,  31, true",
            ">,  30, false",
            "<,  29, true",
            "<,  30, false",
            ">=, 30, true",
            "<=, 30, true",
            "==, 30, true",
            "==, 31, false",
            "!=, 31, false"
    })
    void deveAvaliarOperadores(String operador, double valor, boolean esperado) {
        RegraAlerta regra = new RegraAlerta("temperatura", 30, operador, "TIPO", "msg");

        assertThat(regra.verificar(valor)).isEqualTo(esperado);
    }

    @Test
    void deveInterpolarValorNaMensagem() {
        RegraAlerta regra = new RegraAlerta("temperatura", 45, ">", "TEMPERATURA_ALTA", "Temperatura crítica: {valor}°C");

        assertThat(regra.getMensagemFormatada(50.5)).isEqualTo("Temperatura crítica: 50.5°C");
    }
}
