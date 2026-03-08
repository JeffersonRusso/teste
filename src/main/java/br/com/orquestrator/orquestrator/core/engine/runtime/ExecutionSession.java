package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ExecutionSession: O ponto de entrada para a execução de um pipeline.
 * Agora simplificado para um orquestrador de Dataflow puro.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionSession {

    private final PipelineService pipelineService;
    private final ReactiveExecutionEngine engine;

    /**
     * Executa um pipeline baseado na identidade da requisição e no corpo de dados.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> run(RequestIdentity identity, Map<String, String> headers, Map<String, Object> body) {
        log.info("Iniciando execução para operação: {}", identity.getOperationType());

        // 1. Resolve o Pipeline (Grafo)
        Pipeline pipeline = pipelineService.create(identity);

        // 2. Extrai a parte de 'operation' para o sinal 'raw'
        Map<String, Object> operationBody = (Map<String, Object>) body.getOrDefault("operation", body);

        // 3. Executa o motor de sinais (Dataflow)
        return engine.execute(pipeline, identity, operationBody);
    }
}
