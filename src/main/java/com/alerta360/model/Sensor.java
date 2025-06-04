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
    private String codigoSensor;        // Ex: "TEMP_001", "HUM_SALA_02"

    private String nome;                // Nome amigável
    private String tipo;                // TEMPERATURA, UMIDADE, MULTISENSOR
    private String localizacao;         // Ex: "Sala Principal", "Galpão A"
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime ultimaLeitura;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<LeituraSensor> leituras = new ArrayList<>();
}
