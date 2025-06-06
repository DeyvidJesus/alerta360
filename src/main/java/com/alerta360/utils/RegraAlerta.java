package com.alerta360.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RegraAlerta {
    private String campo;
    private double limite;
    private String operador; // ">", "<", ">=", etc.
    private String tipoAlerta;
    private String mensagem; // com {valor}

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
