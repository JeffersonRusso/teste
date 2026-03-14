package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.fallback;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FallbackInterceptor: Middleware de contingência agnóstico.
 */
@Slf4j
@RequiredArgsConstructor
public final class FallbackInterceptor implements TaskInterceptor {

    private final CompiledConfiguration<FallbackConfig> config;
    private final DataFactory dataFactory;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        String nodeId = context.getTaskName();

        try {
            TaskResult result = chain.proceed(context);
            if (result instanceof TaskResult.Failure) {
                return applyFallback(context, nodeId, null);
            }
            return result;
        } catch (Exception e) {
            return applyFallback(context, nodeId, e);
        }
    }

    private TaskResult applyFallback(TaskExecutionContext context, String nodeId, Exception e) {
        String reason = (e != null) ? e.getMessage() : "Resultado de falha retornado";
        log.warn("Acionando Fallback para o nó [{}]. Motivo: {}", nodeId, reason);
        
        FallbackConfig resolvedConfig = config.resolve(context.getInputs());
        
        // CORREÇÃO: Usa DataFactory para criar o resultado de contingência
        return TaskResult.success(dataFactory.createValue(resolvedConfig.value()));
    }
}
