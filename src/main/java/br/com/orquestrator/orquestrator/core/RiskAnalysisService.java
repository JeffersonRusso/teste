package br.com.orquestrator.orquestrator.core;

import br.com.orquestrator.orquestrator.core.context.*;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
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

    private final ContextFactory contextFactory;
    private final TagManager tagManager;
    private final PipelineService pipelineService;
    private final PipelineEngine engine;
    private final ResultExtractor resultExtractor;

    public Map<String, Object> analyze(RequestIdentity identity, Map<String, String> headers, Map<String, Object> body) {
        // 1. Criação do Contexto com a Identidade já resolvida
        ExecutionContext context = contextFactory.create(identity, headers, body);

        // 2. Preparação
        tagManager.resolveAndApply(context.reader(), context.writer());
        Pipeline pipeline = pipelineService.create(context.metadata());
        
        // 3. Execução
        engine.run(context, pipeline);

        // 4. Extração
        return resultExtractor.extract(context.reader(), pipeline);
    }
}
