package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class LogResponseInterceptor implements TaskInterceptor {

    private final CompiledConfiguration<LogResponseConfig> config;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        String nodeId = context.getTaskName();
        LogResponseConfig resolvedConfig = config.resolve(context.getInputs());

        TaskResult result = chain.proceed(context);
        
        if (log.isInfoEnabled()) {
            switch (result) {
                case TaskResult.Success s -> {
                    if (resolvedConfig.isShowBody()) {
                        log.info("Nó [{}] retornou SUCESSO | Body: {}", nodeId, s.body());
                    } else {
                        log.info("Nó [{}] retornou SUCESSO", nodeId);
                    }
                }
                case TaskResult.Failure f -> log.warn("Nó [{}] retornou FALHA | Mensagem: {}", nodeId, f.errorMessage());
                case TaskResult.Skipped s -> log.info("Nó [{}] foi PULADO", nodeId);
            }
        }
        
        return result;
    }
}
