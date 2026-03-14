package br.com.orquestrator.orquestrator.core.pipeline.compiler;

import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.api.task.Task;

/**
 * InterceptorStackFactory: A "Estratégia" para montar a cadeia de interceptores.
 */
@FunctionalInterface
public interface InterceptorStackFactory {
    Task assemble(Task core, TaskDefinition def);
}
