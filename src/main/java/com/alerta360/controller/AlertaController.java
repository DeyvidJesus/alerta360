package com.alerta360.controller;

import com.alerta360.model.Alerta;
import com.alerta360.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @PostMapping
    public ResponseEntity<Alerta> criarAlertaManual(@RequestBody Alerta alertaJson) {
        Alerta alertaSalvo = alertaService.criarAlertaManual(alertaJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaSalvo);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Alerta>> listarAlertasAtivos() {
        List<Alerta> alertas = alertaService.listarAlertasAtivos();
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/sensor/{codigoSensor}")
    public ResponseEntity<List<Alerta>> listarAlertasPorSensor(@PathVariable String codigoSensor) {
        List<Alerta> alertas = alertaService.listarAlertasPorSensor(codigoSensor);
        return ResponseEntity.ok(alertas);
    }

    @PatchMapping("/{alertaId}/resolver")
    public ResponseEntity<Alerta> resolverAlerta(
            @PathVariable Long alertaId,
            @RequestBody(required = false) Map<String, String> requestBody) {
        String observacoes = requestBody != null ? requestBody.get("observacoes") : null;
        Alerta alerta = alertaService.resolverAlerta(alertaId, observacoes);
        return ResponseEntity.ok(alerta);
    }

    @GetMapping("/estatisticas/tipos")
    public ResponseEntity<Map<String, Long>> contarAlertasPorTipo() {
        Map<String, Long> estatisticas = alertaService.contarAlertasPorTipo();
        return ResponseEntity.ok(estatisticas);
    }

    @PostMapping("/verificar-offline")
    public ResponseEntity<Void> verificarSensoresOffline() {
        alertaService.verificarSensoresOffline();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obterDashboardAlertas() {
        List<Alerta> alertasAtivos = alertaService.listarAlertasAtivos();
        Map<String, Long> alertasPorTipo = alertaService.contarAlertasPorTipo();

        Map<String, Object> dashboard = Map.of(
                "totalAlertasAtivos", alertasAtivos.size(),
                "alertasPorTipo", alertasPorTipo,
                "ultimosAlertas", alertasAtivos.stream().limit(5).toList()
        );

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/tipo/{tipoAlerta}")
    public ResponseEntity<List<Alerta>> listarAlertasPorTipo(@PathVariable String tipoAlerta) {
        List<Alerta> alertas = alertaService.listarAlertasAtivos().stream()
                .filter(alerta -> alerta.getTipoAlerta().equals(tipoAlerta))
                .toList();
        return ResponseEntity.ok(alertas);
    }
}