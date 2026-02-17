package br.com.orquestrator.orquestrator.service;

import br.com.orquestrator.orquestrator.core.context.RiskContextFactory;
import br.com.orquestrator.orquestrator.core.engine.PipelineEngine;
import br.com.orquestrator.orquestrator.core.engine.ResultExtractor;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.AnalysisRequest;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RiskAnalysisService: Orquestrador de alto nível.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final RiskContextFactory contextFactory;
    private final PipelineService pipelineService;
    private final PipelineEngine engine;
    private final ResultExtractor resultExtractor;

    public Map<String, Object> analyze(AnalysisRequest request) {
        // 1. Criação do Contexto
        ExecutionContext context = contextFactory.create(
            request.operationType(), 
            request.headers(), 
            request.body()
        );

        // 2. Criação do Pipeline (Via Fachada SOLID)
        Pipeline pipeline = pipelineService.create(context, request.requiredOutputs());
        
        // 3. Execução
        engine.run(context, pipeline);

        // 4. Extração
        return resultExtractor.extract(context, pipeline);
    }
}
