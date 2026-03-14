package br.com.orquestrator.orquestrator.core.engine.observability;

/**
 * PipelineEventListener: Contrato unificado para observabilidade.
 */
@FunctionalInterface
public interface PipelineEventListener {
    void onEvent(PipelineEvent event);
}
