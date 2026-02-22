package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Interceptor de Observabilidade: Único dono do rastro de sistema.
 */
@Slf4j
@Component("OBSERVABILITY_INTERCEPTOR")
public class ObservabilityInterceptor {

    public TaskInterceptor create(String nodeId) {
        return next -> context -> {
            // Inicia o rastro técnico (Span)
            try (var span = context.getTrace().startSpan(nodeId, "TASK")) {
                try {
                    var result = next.proceed(context);
                    span.setStatus(result.status());
                    return result;
                } catch (Exception e) {
                    span.fail(e);
                    // CORREÇÃO: Relança como RuntimeException para satisfazer a interface funcional
                    throw e instanceof RuntimeException re ? re : new RuntimeException(e);
                }
            }
        };
    }
}
