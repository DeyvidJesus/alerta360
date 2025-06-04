package com.alerta360.repository;

import com.alerta360.model.Alerta;
import com.alerta360.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Listar alertas não resolvidos ordenados por data
    List<Alerta> findByResolvidoFalseOrderByDataHoraDesc();

    // Listar alertas por sensor
    List<Alerta> findBySensorCodigoSensorOrderByDataHoraDesc(String codigoSensor);

    // Verificar se existe alerta similar recente
    boolean existsByTipoAlertaAndSensorAndDataHoraAfterAndResolvidoFalse(
            String tipoAlerta, Sensor sensor, LocalDateTime dataHora);

    // Verificar se existe alerta específico não resolvido
    boolean existsByTipoAlertaAndSensorAndResolvidoFalse(String tipoAlerta, Sensor sensor);
}