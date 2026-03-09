package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;

/**
 * OBSOLETO: A lógica de decoração agora é feita via InterceptorTask e DecoratorPipelineBuilder.
 */
@RequiredArgsConstructor
public abstract class TaskDecorator implements Task {
    protected final Task decoratedTask;
}
