package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.core.context.OperationTypeResolver;
import br.com.orquestrator.orquestrator.domain.vo.AnalysisRequest;
import br.com.orquestrator.orquestrator.service.RiskAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/analise")
@RequiredArgsConstructor
public class AnalysisController {

    private final RiskAnalysisService riskAnalysisService;
    private final OperationTypeResolver operationTypeResolver;

    @PostMapping
    public ResponseEntity<Map<String, Object>> analyze(
            @RequestHeader Map<String, String> headers,
            @RequestBody Map<String, Object> rawBody) {

        String operationType = operationTypeResolver.resolve(headers, rawBody);

        // O rawBody agora é um Map puro.
        AnalysisRequest request = AnalysisRequest.simple(operationType, headers, rawBody);
        
        Map<String, Object> response = riskAnalysisService.analyze(request);

        return ResponseEntity.ok(response);
    }
}
