package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.core.RiskAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AnalysisController: Ponto de entrada da API de Análise de Risco.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analise")
@RequiredArgsConstructor
public class AnalysisController {

    private final RiskAnalysisService riskAnalysisService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> analyze(
            @RequestHeader Map<String, String> headers,
            @RequestBody Map<String, Object> rawBody) {

        log.info("Recebendo requisição de análise. CorrelationId: {}", headers.get("x-correlation-id"));

        Map<String, Object> response = riskAnalysisService.analyze(headers, rawBody);

        return ResponseEntity.ok(response);
    }
}
