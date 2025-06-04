package com.alerta360.repository;

import com.alerta360.model.LeituraSensor;
import com.alerta360.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {

    // Contar leituras por sensor
    long countBySensor(Sensor sensor);

    // Buscar última leitura de um sensor
    Optional<LeituraSensor> findTopBySensorOrderByTimestampDesc(Sensor sensor);

    // Buscar N últimas leituras de um sensor
    @Query("SELECT l FROM LeituraSensor l WHERE l.sensor = :sensor ORDER BY l.timestamp DESC")
    List<LeituraSensor> findTopNBySensorOrderByTimestampDesc(@Param("sensor") Sensor sensor, Pageable pageable);

    // Método helper para buscar N leituras
    default List<LeituraSensor> findTopNBySensorOrderByTimestampDesc(Sensor sensor, int limit) {
        return findTopNBySensorOrderByTimestampDesc(sensor, PageRequest.of(0, limit));
    }

    // Buscar leituras por período
    List<LeituraSensor> findBySensorAndTimestampBetween(Sensor sensor, LocalDateTime inicio, LocalDateTime fim);

    // Buscar leituras por status
    List<LeituraSensor> findBySensorAndStatus(Sensor sensor, String status);

    // Buscar leituras por período e status
    List<LeituraSensor> findBySensorAndTimestampBetweenAndStatus(
            Sensor sensor, LocalDateTime inicio, LocalDateTime fim, String status);

    // Buscar última leitura de cada sensor (para verificar sensores offline)
    @Query("SELECT l FROM LeituraSensor l WHERE l.id IN (" +
            "SELECT MAX(l2.id) FROM LeituraSensor l2 GROUP BY l2.sensor.id)")
    List<LeituraSensor> findUltimaLeituraPorSensor();

    // Buscar leituras antigas para limpeza
    List<LeituraSensor> findByTimestampBefore(LocalDateTime dataLimite);

    // Estatísticas - contar leituras por status em um período
    @Query("SELECT l.status, COUNT(l) FROM LeituraSensor l " +
            "WHERE l.sensor = :sensor AND l.timestamp BETWEEN :inicio AND :fim " +
            "GROUP BY l.status")
    List<Object[]> countLeiturasPorStatusPorPeriodo(
            @Param("sensor") Sensor sensor,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Buscar leituras com alertas
    @Query("SELECT l FROM LeituraSensor l WHERE l.status IN ('ALERTA', 'ERRO') " +
            "AND l.timestamp >= :inicio ORDER BY l.timestamp DESC")
    List<LeituraSensor> findLeiturasComAlertasRecentes(@Param("inicio") LocalDateTime inicio);
}
