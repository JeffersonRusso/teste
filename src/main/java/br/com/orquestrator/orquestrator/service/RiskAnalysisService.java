package br.com.orquestrator.orquestrator.service;

import br.com.orquestrator.orquestrator.core.context.FlowRouter;
import br.com.orquestrator.orquestrator.core.context.RiskContextFactory;
import br.com.orquestrator.orquestrator.core.engine.PipelineEngine;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineManager;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orquestrador principal de análise de risco.
 * Coordena o ciclo de vida da execução: Contexto -> Roteamento -> Pipeline -> Motor -> Resposta.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final RiskContextFactory contextFactory;
    private final PipelineManager pipelineManager;
    private final PipelineEngine engine;
    private final FlowRouter flowRouter;

    public Map<String, Object> analyze(final String operationType, final Map<String, String> headers, final JsonNode rawBody) {
        return analyze(operationType, headers, rawBody, null);
    }

    public Map<String, Object> analyze(final String operationType, 
                                       final Map<String, String> headers, 
                                       final JsonNode rawBody, 
                                       final Set<String> requiredOutputs) {
        
        // 1. Inicialização do Contexto (Normalização inclusa)
        final ExecutionContext context = contextFactory.create(operationType, headers, rawBody);
        
        // 2. Roteamento Dinâmico (Canary/Versão)
        final Integer version = flowRouter.resolveVersion(operationType);
        if (version != null) {
            context.addTaskMetadata("system", "flow_version", version);
        }
        
        // 3. Construção do Pipeline (Otimizado por dependências)
        final Pipeline pipeline = pipelineManager.createAndValidate(context, requiredOutputs, version);
        
        // 4. Execução Orquestrada
        engine.run(context, pipeline);

        // 5. Extração do Resultado Final (Baseado no contrato do Pipeline)
        return extractResults(context, pipeline);
    }

    private Map<String, Object> extractResults(ExecutionContext context, Pipeline pipeline) {
        return pipeline.getRequiredOutputs().stream()
                .filter(context::has)
                .collect(Collectors.toMap(
                        target -> target,
                        context::get,
                        (existing, _) -> existing
                ));
    }
}
