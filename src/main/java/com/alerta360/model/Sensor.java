package com.alerta360.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensores")
@Getter
@Setter
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoSensor;

    private String nome;
    private String tipo;
    private String localizacao;
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime ultimaLeitura;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<LeituraSensor> leituras = new ArrayList<>();
}
