package br.com.orquestrator.orquestrator.core;

import br.com.orquestrator.orquestrator.core.context.OperationTypeResolver;
import br.com.orquestrator.orquestrator.core.context.RiskContextFactory;
import br.com.orquestrator.orquestrator.core.context.tag.TagManager;
import br.com.orquestrator.orquestrator.core.engine.PipelineEngine;
import br.com.orquestrator.orquestrator.core.engine.binding.ResultExtractor;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RiskAnalysisService: Fachada de entrada do orquestrador.
 * Coordena a criação do contexto, resolução de tags, carga do pipeline e execução.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final OperationTypeResolver operationTypeResolver;
    private final RiskContextFactory contextFactory;
    private final TagManager tagManager;
    private final PipelineService pipelineService;
    private final PipelineEngine engine;
    private final ResultExtractor resultExtractor;

    public Map<String, Object> analyze(Map<String, String> headers, Map<String, Object> body) {
        // 1. Identifica a operação
        String operationType = operationTypeResolver.resolve(headers, body);

        // 2. Cria o contexto base
        ExecutionContext context = contextFactory.execute(operationType, headers, body);

        // 3. Resolve os cenários (Tags) - ESSENCIAL para o Tree Shaking
        tagManager.resolveAndApply(context);

        // 4. Obtém o pipeline otimizado para este cenário
        Pipeline pipeline = pipelineService.create(context);
        
        // 5. Executa o motor reativo
        engine.run(context, pipeline);

        // 6. Extrai os resultados finais solicitados
        return resultExtractor.extract(context, pipeline);
    }
}
