package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import java.util.List;

/**
 * InterceptorEngine: O único especialista em extensões de tarefas.
 * Centraliza a criação de decoradores de resiliência, cache e logs.
 */
public interface InterceptorEngine {
    /**
     * Transforma definições de features em decoradores executáveis.
     */
    List<TaskDecorator> resolveInterceptors(List<FeatureDefinition> features, String nodeId);
}
