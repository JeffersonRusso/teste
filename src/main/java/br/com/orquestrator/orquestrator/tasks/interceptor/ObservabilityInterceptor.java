package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Interceptor de Observabilidade: Único dono do rastro de sistema.
 */
@Slf4j
@Component("OBSERVABILITY")
public class ObservabilityInterceptor implements TaskInterceptor {

    @Override
    public TaskResult intercept(ExecutionContext context, TaskChain next, Object config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();
        
        // Inicia o rastro técnico (Span)
        try (var span = context.getTrace().startSpan(nodeId, taskDef.getType())) {
            try {
                TaskResult result = next.proceed(context);
                span.setStatus(result.status());
                return result;
            } catch (Exception e) {
                span.fail(e);
                throw e;
            }
        }
    }
}
