package com.alerta360.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Getter
@Setter
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "leitura_id")
    private LeituraSensor leitura;

    private String tipoAlerta;
    private String mensagem;
    private LocalDateTime dataHora;
    private boolean resolvido;
    private LocalDateTime dataResolucao;
}