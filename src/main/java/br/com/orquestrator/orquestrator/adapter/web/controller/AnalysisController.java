package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.core.RiskAnalysisService;
import br.com.orquestrator.orquestrator.core.context.identity.IdentityResolver;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        
        // 1. Define a identidade soberana logo na entrada
        RequestIdentity identity = identityResolver.resolve(headers, body);
        
        log.info("Iniciando análise. CorrelationId: {} | ExecutionId: {} | Operation: {}", 
                identity.correlationId(), identity.executionId(), identity.operationType());

        // 2. Passa a identidade completa para o serviço
        return riskAnalysisService.analyze(identity, headers, body);
    }
}
