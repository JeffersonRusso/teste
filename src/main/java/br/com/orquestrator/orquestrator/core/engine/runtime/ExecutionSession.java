package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ExecutionContext;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * ExecutionSession: Encapsula o ciclo de vida de uma única execução de pipeline.
 * Garante que as fases (Normalização, Execução, Observabilidade) ocorram na ordem correta.
 */
@RequiredArgsConstructor
public class ExecutionSession {

    private final ExecutionContext context;
    private final Pipeline pipeline;
    private final DataMarshaller marshaller;
    private final ReactiveExecutionEngine engine;
    private final PipelineEventPublisher eventPublisher;

    public void run() {
        boolean success = false;
        try {
            // Fase 1: Preparação (Normalização)
            marshaller.executeNormalization(pipeline.normalizationPlan(), context.writer());

            // Fase 2: Execução Core
            engine.execute(pipeline);
            
            success = true;
        } catch (Exception e) {
            throw (e instanceof PipelineException pe) ? pe : new PipelineException("Erro na sessão de execução", e);
        } finally {
            // Fase 3: Finalização (Observabilidade)
            eventPublisher.publishFinished(context.metadata(), success);
        }
    }

    public Map<String, Object> extractResults(br.com.orquestrator.orquestrator.core.engine.binding.ResultExtractor extractor) {
        return extractor.extract(context.reader(), pipeline);
    }
}
