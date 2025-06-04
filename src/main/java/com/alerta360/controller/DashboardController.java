package com.alerta360.controller;

import com.alerta360.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> obterResumoGeral() {
        Map<String, Object> resumo = dashboardService.obterResumoGeral();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/sensores/status")
    public ResponseEntity<List<Map<String, Object>>> obterStatusTodosSensores() {
        List<Map<String, Object>> statusSensores = dashboardService.obterUltimasLeiturasTodosSensores();
        return ResponseEntity.ok(statusSensores);
    }

    @GetMapping("/monitoramento")
    public ResponseEntity<Map<String, Object>> obterDadosMonitoramento() {
        Map<String, Object> resumo = dashboardService.obterResumoGeral();
        List<Map<String, Object>> statusSensores = dashboardService.obterUltimasLeiturasTodosSensores();

        Map<String, Object> monitoramento = Map.of(
                "resumoGeral", resumo,
                "statusSensores", statusSensores,
                "timestamp", java.time.LocalDateTime.now()
        );

        return ResponseEntity.ok(monitoramento);
    }

    @GetMapping("/sensores/ativos")
    public ResponseEntity<List<Map<String, Object>>> obterSensoresAtivos() {
        List<Map<String, Object>> sensores = dashboardService.obterUltimasLeiturasTodosSensores();
        List<Map<String, Object>> sensoresAtivos = sensores.stream()
                .filter(sensor -> !"SEM_DADOS".equals(sensor.get("status")))
                .toList();

        return ResponseEntity.ok(sensoresAtivos);
    }

    @GetMapping("/sensores/inativos")
    public ResponseEntity<List<Map<String, Object>>> obterSensoresInativos() {
        List<Map<String, Object>> sensores = dashboardService.obterUltimasLeiturasTodosSensores();
        List<Map<String, Object>> sensoresInativos = sensores.stream()
                .filter(sensor -> "SEM_DADOS".equals(sensor.get("status")))
                .toList();

        return ResponseEntity.ok(sensoresInativos);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> verificarSaude() {
        Map<String, Object> resumo = dashboardService.obterResumoGeral();

        int totalSensores = (Integer) resumo.get("totalSensores");
        int sensoresAtivos = (Integer) resumo.get("sensoresAtivos");
        int alertasAtivos = (Integer) resumo.get("alertasAtivos");

        String status;
        if (alertasAtivos > 5) {
            status = "CRITICO";
        } else if (alertasAtivos > 0 || sensoresAtivos < totalSensores * 0.8) {
            status = "ATENCAO";
        } else {
            status = "OK";
        }

        Map<String, Object> saude = Map.of(
                "status", status,
                "totalSensores", totalSensores,
                "sensoresAtivos", sensoresAtivos,
                "alertasAtivos", alertasAtivos,
                "percentualAtivos", totalSensores > 0 ? (sensoresAtivos * 100.0 / totalSensores) : 0,
                "timestamp", java.time.LocalDateTime.now()
        );

        return ResponseEntity.ok(saude);
    }
}