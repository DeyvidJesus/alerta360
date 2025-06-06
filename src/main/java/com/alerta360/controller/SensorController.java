package com.alerta360.controller;

import com.alerta360.model.Sensor;
import com.alerta360.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensores")
@CrossOrigin(origins = "*")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @PostMapping
    public ResponseEntity<Sensor> criarSensor(@RequestBody Sensor sensor) {
        Sensor sensorCriado = sensorService.criarSensor(sensor);
        return new ResponseEntity<>(sensorCriado, HttpStatus.CREATED);
    }

    @GetMapping("/{codigoSensor}")
    public ResponseEntity<Sensor> buscarSensor(@PathVariable String codigoSensor) {
        Sensor sensor = sensorService.buscarPorCodigo(codigoSensor);
        return ResponseEntity.ok(sensor);
    }

    @GetMapping
    public ResponseEntity<List<Sensor>> listarTodos() {
        List<Sensor> sensores = sensorService.listarTodos();
        return ResponseEntity.ok(sensores);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Sensor>> listarAtivos() {
        List<Sensor> sensores = sensorService.listarAtivos();
        return ResponseEntity.ok(sensores);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Sensor>> listarPorTipo(@PathVariable String tipo) {
        List<Sensor> sensores = sensorService.listarPorTipo(tipo.toLowerCase());
        return ResponseEntity.ok(sensores);
    }

    @PutMapping("/{codigoSensor}")
    public ResponseEntity<Sensor> atualizarSensor(
            @PathVariable String codigoSensor,
            @RequestBody Sensor dadosAtualizacao) {
        Sensor sensor = sensorService.atualizarSensor(codigoSensor, dadosAtualizacao);
        return ResponseEntity.ok(sensor);
    }

    @PatchMapping("/{codigoSensor}/status")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable String codigoSensor,
            @RequestParam boolean ativo) {
        sensorService.ativarDesativarSensor(codigoSensor, ativo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{codigoSensor}")
    public ResponseEntity<Void> deletarSensor(@PathVariable String codigoSensor) {
        sensorService.deletarSensor(codigoSensor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codigoSensor}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable String codigoSensor) {
        Map<String, Object> estatisticas = sensorService.obterEstatisticasSensor(codigoSensor);
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<Sensor>> buscarSensoresInativos(
            @RequestParam(defaultValue = "24") int horasSemLeitura) {
        List<Sensor> sensores = sensorService.buscarSensoresInativos(horasSemLeitura);
        return ResponseEntity.ok(sensores);
    }
}