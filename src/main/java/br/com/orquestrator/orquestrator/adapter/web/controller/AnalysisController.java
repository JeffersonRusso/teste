package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.core.RiskAnalysisService;
import br.com.orquestrator.orquestrator.core.context.identity.IdentityResolver;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AnalysisController: Ponto de entrada para as análises de risco.
 * Gerencia a identidade soberana da requisição.
 */
@Slf4j
@RestController
@RequestMapping("/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final RiskAnalysisService riskAnalysisService;
    private final IdentityResolver identityResolver;

    @PostMapping
    public Map<String, Object> analyze(@RequestHeader Map<String, String> headers, 
                                       @RequestBody Map<String, Object> body) {
        
        // 1. Resolve a identidade soberana
        RequestIdentity identity = identityResolver.resolve(headers, body);
        
        log.info("Iniciando análise. CorrelationId: {} | ExecutionId: {} | Operation: {}", 
                identity.correlationId(), identity.executionId(), identity.operationType());

        // 2. Executa o serviço de análise
        return riskAnalysisService.analyze(identity, headers, body);
    }
}
