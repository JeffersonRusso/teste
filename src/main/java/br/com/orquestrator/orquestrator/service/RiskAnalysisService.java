package br.com.orquestrator.orquestrator.service;

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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final RiskContextFactory contextFactory;
    private final PipelineManager pipelineManager;
    private final PipelineEngine engine;

    public Map<String, Object> analyze(final String operationType, final Map<String, String> headers, final JsonNode rawBody) {
        return analyze(operationType, headers, rawBody, null);
    }

    public Map<String, Object> analyze(final String operationType, 
                                       final Map<String, String> headers, 
                                       final JsonNode rawBody, 
                                       final Set<String> requiredOutputs) {
        
        final ExecutionContext context = contextFactory.create(operationType, headers, rawBody);
        final Pipeline pipeline = pipelineManager.createAndValidate(context, requiredOutputs, null);
        
        engine.run(context, pipeline);

        return extractResults(context, pipeline);
    }

    private Map<String, Object> extractResults(ExecutionContext context, Pipeline pipeline) {
        return pipeline.getRequiredOutputs().stream()
                .filter(key -> context.get(key) != null)
                .collect(Collectors.toMap(
                        key -> key,
                        key -> context.get(key),
                        (existing, replacement) -> existing
                ));
    }
}
