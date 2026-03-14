package br.com.orquestrator.orquestrator.tasks.interceptor.impl.guard;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GuardInterceptor: Avalia dinamicamente se a tarefa deve ser executada.
 * 
 * Agora seguindo o padrão de configuração compilada para máxima consistência.
 */
@Slf4j
@RequiredArgsConstructor
public final class GuardInterceptor implements TaskInterceptor {

    private final ExpressionEngine expressionEngine;
    private final CompiledConfiguration<GuardConfig> config;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        // Resolve a condição de guarda usando os inputs atuais
        GuardConfig resolvedConfig = config.resolve(context.getInputs());
        String condition = resolvedConfig.condition();

        if (condition == null || condition.isBlank()) {
            return chain.proceed(context);
        }

        try {
            Boolean shouldExecute = expressionEngine.compile(condition)
                    .evaluate(context.getInputs(), Boolean.class);

            if (Boolean.TRUE.equals(shouldExecute)) {
                return chain.proceed(context);
            }

            log.info("Tarefa [{}] ignorada: condição [{}] não atendida.", 
                     context.getTaskName(), condition);
            return new TaskResult.Skipped();

        } catch (Exception e) {
            log.error("Erro ao avaliar guarda para [{}]: {}", context.getTaskName(), e.getMessage());
            return new TaskResult.Failure("Erro na guarda: " + e.getMessage(), 500);
        }
    }
}
