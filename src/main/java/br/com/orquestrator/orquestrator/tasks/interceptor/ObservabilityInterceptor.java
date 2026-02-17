package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Interceptor de Observabilidade: Único dono do rastro de sistema.
 */
@Slf4j
@Component("OBSERVABILITY")
public class ObservabilityInterceptor implements TaskInterceptor {

    @Override
    public Object intercept(ExecutionContext context, TaskChain next, Object config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();
        Instant start = Instant.now();
        
        var span = context.getTracker().getSpan(nodeId).orElse(null);
        if (span != null) span.addMetadata("task.type", taskDef.getType());

        try {
            Object result = next.proceed(context);
            
            if (span != null && span.toMetrics().metadata().get("status") == null) {
                span.addMetadata("status", 200);
            }
            return result;

        } catch (Exception e) {
            if (span != null) {
                span.addMetadata("status", 500);
                span.addMetadata("error", e.getMessage());
            }
            throw e;
        } finally {
            long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
            if (span != null) span.addMetadata("execution.duration_ms", duration);
        }
    }
}
