package br.com.orquestrator.orquestrator.tasks.interceptor.resilience;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import java.util.function.Supplier;

/**
 * Estratégia para envolver uma execução com padrões de resiliência.
 */
public interface ResilienceStrategy<C> {
    TaskResult execute(Supplier<TaskResult> execution, String resourceId, C config);
    String getType();
}
