package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("FALLBACK")
public class FallbackInterceptor extends TypedTaskInterceptor<FallbackConfig> {

    private final ObjectMapper objectMapper;

    public FallbackInterceptor(ObjectMapper objectMapper) {
        super(FallbackConfig.class);
        this.objectMapper = objectMapper;
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, FallbackConfig config, TaskDefinition taskDef) {
        try {
            next.proceed(data);
        } catch (Exception e) {
            log.warn("Task {} falhou. Aplicando Fallback.", taskDef.getNodeId());
            
            data.addMetadata("fallback.applied", true);
            data.addMetadata("fallback.reason", e.getMessage());
            
            applyFallback(data, config, taskDef);
        }
    }

    private void applyFallback(TaskData data, FallbackConfig config, TaskDefinition taskDef) {
        if (config.value() == null) return;

        List<DataSpec> produces = taskDef.getProduces();
        if (produces == null || produces.isEmpty()) return;

        Object fallbackValue = objectMapper.convertValue(config.value(), Object.class);

        if (produces.size() == 1) {
            data.put(produces.getFirst().name(), fallbackValue);
        } else if (fallbackValue instanceof Map<?, ?> fallbackMap) {
            for (int i = 0; i < produces.size(); i++) {
                String name = produces.get(i).name();
                if (fallbackMap.containsKey(name)) {
                    data.put(name, fallbackMap.get(name));
                }
            }
        }
    }
}
