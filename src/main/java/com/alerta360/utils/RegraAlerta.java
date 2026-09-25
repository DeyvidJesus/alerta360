package com.alerta360.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Regra de threshold aplicada a um campo numérico do payload de uma leitura.
 * Ex.: {@code new RegraAlerta("temperatura", 45, ">", "TEMPERATURA_ALTA", "Temperatura crítica: {valor}°C")}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegraAlerta {
    private String campo;
    private double limite;
    private String operador; // ">", "<", ">=", "<=", "=="
    private String tipoAlerta;
    private String mensagem; // aceita o placeholder {valor}

    public boolean verificar(Double valor) {
        return switch (operador) {
            case ">" -> valor > limite;
            case "<" -> valor < limite;
            case ">=" -> valor >= limite;
            case "<=" -> valor <= limite;
            case "==" -> valor == limite;
            default -> false;
        };
    }

    public String getMensagemFormatada(Double valor) {
        return mensagem.replace("{valor}", String.valueOf(valor));
    }
}
