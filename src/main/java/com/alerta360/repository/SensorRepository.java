package com.alerta360.repository;

import com.alerta360.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    // Buscar por código do sensor
    Optional<Sensor> findByCodigoSensor(String codigoSensor);

    // Verificar se código já existe
    boolean existsByCodigoSensor(String codigoSensor);

    // Listar sensores ativos
    List<Sensor> findByAtivoTrue();

    // Listar por tipo
    List<Sensor> findByTipo(String tipo);

    // Buscar sensores sem leitura recente (para verificar inativos)
    @Query("SELECT s FROM Sensor s WHERE s.ultimaLeitura IS NULL OR s.ultimaLeitura < :limite")
    List<Sensor> findSensoresSemLeituraRecente(@Param("limite") LocalDateTime limite);

    // Buscar sensores ativos por localização
    List<Sensor> findByAtivoTrueAndLocalizacaoContaining(String localizacao);

    // Contar sensores por tipo
    @Query("SELECT s.tipo, COUNT(s) FROM Sensor s GROUP BY s.tipo")
    List<Object[]> countSensoresPorTipo();
}