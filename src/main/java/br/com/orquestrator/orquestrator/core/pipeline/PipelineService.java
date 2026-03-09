package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.engine.runtime.ReactiveExecutionEngine;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineLoader loader;
    private final PipelineCompiler compiler;
    private final ReactiveExecutionEngine engine;

    public Map<String, Object> execute(RequestIdentity identity, Map<String, Object> input) {
        // 1. Carrega a definição do pipeline
        PipelineDefinition definition = loader.loadDefinition(identity.getOperationType());

        // 2. Compila o pipeline para a execução atual
        Pipeline pipeline = compiler.compile(definition, identity.getActiveTags());
        
        // 3. Executa
        return engine.execute(pipeline, identity, input);
    }
}
