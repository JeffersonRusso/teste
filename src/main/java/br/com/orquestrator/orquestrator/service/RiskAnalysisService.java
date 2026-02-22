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
 * Java 21: Fluxo declarativo e limpo.
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
        ExecutionContext context = contextFactory.execute(
            request.operationType(), 
            request.headers(), 
            request.body()
        );

        // 2. Montagem do Pipeline
        Pipeline pipeline = pipelineService.create(context, request.requiredOutputs());
        
        // 3. Execução (A Engine agora cuida da inicialização e das tasks)
        engine.run(context, pipeline);

        // 4. Extração do Resultado
        return resultExtractor.extract(context, pipeline);
    }
}
