package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.tasks.base.Task;

/**
 * OBSOLETO: A lógica de decoração agora é feita via InterceptorTask e DecoratorPipelineBuilder.
 */
public interface TaskDecorator {
    Task decorate(Task task);
}
