package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import java.util.function.Function;

/**
 * Contrato fundamental de um Interceptor.
 */
@FunctionalInterface
public interface TaskInterceptor extends Function<TaskChain, TaskChain> {
}
