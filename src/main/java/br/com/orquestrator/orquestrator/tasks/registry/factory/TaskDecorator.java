package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.InterceptorStack;
import br.com.orquestrator.orquestrator.tasks.interceptor.InterceptorStep;
import br.com.orquestrator.orquestrator.tasks.interceptor.TaskInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TaskDecorator: Especialista em envolver tasks com interceptores (Features).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDecorator {

    private final Map<String, TaskInterceptor> interceptors;
    private final ObjectMapper objectMapper;

    public Task decorate(Task core, TaskDefinition def) {
        List<InterceptorStep> steps = def.getAllFeaturesOrdered().stream()
                .map(feat -> {
                    TaskInterceptor interceptor = interceptors.get(feat.type().toUpperCase());
                    if (interceptor == null) return null;

                    Object config = (interceptor.getConfigClass() != null)
                            ? objectMapper.convertValue(feat.config(), interceptor.getConfigClass())
                            : feat.config();

                    return new InterceptorStep(interceptor, config);
                })
                .filter(Objects::nonNull)
                .toList();

        return steps.isEmpty() ? core : new InterceptorStack(core, steps, def);
    }
}
