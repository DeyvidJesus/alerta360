package com.alerta360.controller;

import com.alerta360.model.LeituraSensor;
import com.alerta360.service.LeituraSensorService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leituras")
@CrossOrigin(origins = "*")
public class LeituraSensorController {

    @Autowired
    private LeituraSensorService leituraService;

    @PostMapping("/{codigoSensor}")
    public ResponseEntity<LeituraSensor> processarDados(
            @PathVariable String codigoSensor,
            @RequestBody LeituraSensor leitura) {

        LeituraSensor novaLeitura = leituraService.processarLeituraExistente(codigoSensor, leitura);
        return new ResponseEntity<>(novaLeitura, HttpStatus.CREATED);
    }

    @GetMapping("/{codigoSensor}")
    public ResponseEntity<List<Map<String, Object>>> obterDadosFormatados(
            @PathVariable String codigoSensor,
            @RequestParam(defaultValue = "10") int limite) {
        List<Map<String, Object>> dados = leituraService.obterDadosFormatados(codigoSensor, limite);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/{codigoSensor}/periodo")
    public ResponseEntity<List<Map<String, Object>>> obterDadosPorPeriodo(
            @PathVariable String codigoSensor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Map<String, Object>> dados = leituraService.obterDadosPorPeriodo(codigoSensor, inicio, fim);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/{codigoSensor}/estatisticas")
    public ResponseEntity<Map<String, Object>> calcularEstatisticasPeriodo(
            @PathVariable String codigoSensor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        Map<String, Object> estatisticas = leituraService.calcularEstatisticasPeriodo(codigoSensor, inicio, fim);
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/{codigoSensor}/historico")
    public ResponseEntity<List<Map<String, Object>>> obterHistorico(
            @PathVariable String codigoSensor,
            @RequestParam(defaultValue = "24") int ultimasHoras) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusHours(ultimasHoras);
        List<Map<String, Object>> dados = leituraService.obterDadosPorPeriodo(codigoSensor, inicio, fim);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/{codigoSensor}/ultima")
    public ResponseEntity<List<Map<String, Object>>> obterUltimaLeitura(@PathVariable String codigoSensor) {
        List<Map<String, Object>> dados = leituraService.obterDadosFormatados(codigoSensor, 1);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/{codigoSensor}/hoje")
    public ResponseEntity<List<Map<String, Object>>> obterDadosHoje(@PathVariable String codigoSensor) {
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fim = LocalDateTime.now();
        List<Map<String, Object>> dados = leituraService.obterDadosPorPeriodo(codigoSensor, inicio, fim);
        return ResponseEntity.ok(dados);
    }
}