package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ExecutionSession: Ponto de entrada para a execução de um pipeline.
 * Orquestra o carregamento, compilação e execução.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionSession {

    private final PipelineService pipelineService;

    public Map<String, Object> execute(RequestIdentity identity, Map<String, Object> input) {
        log.info("Iniciando execução para operação: {}", identity.getOperationType());
        return pipelineService.execute(identity, input);
    }
}
