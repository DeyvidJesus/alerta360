package com.alerta360.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "leituras_sensores")
@Getter
@Setter
public class LeituraSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(columnDefinition = "JSON")
    private String dados;

    private LocalDateTime timestamp;
    private String status;

    @Transient
    public Map<String, Object> getDadosMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(dados, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void setDadosMap(Map<String, Object> dadosMap) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.dados = mapper.writeValueAsString(dadosMap);
        } catch (Exception e) {
            this.dados = "{}";
        }
    }
}