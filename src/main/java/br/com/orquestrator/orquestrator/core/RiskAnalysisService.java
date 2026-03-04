package br.com.orquestrator.orquestrator.core;

import br.com.orquestrator.orquestrator.core.context.*;
import br.com.orquestrator.orquestrator.core.context.identity.CorrelationIdResolver;
import br.com.orquestrator.orquestrator.core.context.tag.TagManager;
import br.com.orquestrator.orquestrator.core.engine.PipelineEngine;
import br.com.orquestrator.orquestrator.core.engine.binding.ResultExtractor;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final OperationTypeResolver operationTypeResolver;
    private final CorrelationIdResolver correlationIdResolver; // <--- Novo Especialista
    private final ContextFactory contextFactory;
    private final TagManager tagManager;
    private final PipelineService pipelineService;
    private final PipelineEngine engine;
    private final ResultExtractor resultExtractor;

    public Map<String, Object> analyze(Map<String, String> headers, Map<String, Object> body) {
        // 1. Identificação (Fronteira)
        String operationType = operationTypeResolver.resolve(headers, body);
        String correlationId = correlationIdResolver.resolve(headers);

        // 2. Criação do Contexto
        ExecutionContext context = contextFactory.create(correlationId, operationType, headers, body);

        // 3. Preparação
        tagManager.resolveAndApply(context.reader(), context.writer());
        Pipeline pipeline = pipelineService.create(context.metadata());
        
        // 4. Execução
        engine.run(context, pipeline);

        // 5. Extração
        return resultExtractor.extract(context.reader(), pipeline);
    }
}
