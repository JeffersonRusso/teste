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

/**
 * Interceptor responsável por aplicar valores de fallback em caso de falha na execução da task.
 * Garante que o pipeline continue fluindo mesmo com falhas em nós não críticos.
 * Java 21: Refatorado para respeitar interrupções e utilizar String Templates.
 */
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
            // Regra de Ouro (Java 21): Nunca capture InterruptedException no fallback.
            // Precisamos permitir que o motor (StructuredTaskScope) cancele a thread graciosamente.
            if (Thread.currentThread().isInterrupted()) {
                throw (RuntimeException) e;
            }

            handleFallback(data, config, taskDef, e);
        }
    }

    private void handleFallback(TaskData data, FallbackConfig config, TaskDefinition taskDef, Exception e) {
        String nodeId = taskDef.getNodeId().value();
        // Java 21: String Templates para logs claros
        log.warn(STR."Task '\{nodeId}' failed. Applying fallback logic. Reason: \{e.getMessage()}");

        // Enriquecimento do rastro de observabilidade
        data.addMetadata("fallback.applied", true);
        data.addMetadata("fallback.error_type", e.getClass().getSimpleName());

        applyValues(data, config, taskDef);
    }

    private void applyValues(TaskData data, FallbackConfig config, TaskDefinition taskDef) {
        if (config.value() == null) return;

        List<DataSpec> produces = taskDef.getProduces();
        if (produces == null || produces.isEmpty()) return;

        // Converte o valor de fallback do JSON para um objeto Java
        Object fallbackValue = objectMapper.convertValue(config.value(), Object.class);

        // Java 21: Pattern Matching e SequencedCollections para distribuição de dados
        if (produces.size() == 1) {
            data.put(produces.getFirst().name(), fallbackValue);
        } else if (fallbackValue instanceof Map<?, ?> fallbackMap) {
            distributeMapValues(data, produces, fallbackMap);
        }
    }

    private void distributeMapValues(TaskData data, List<DataSpec> produces, Map<?, ?> fallbackMap) {
        produces.forEach(spec -> {
            String name = spec.name();
            if (fallbackMap.containsKey(name)) {
                data.put(name, fallbackMap.get(name));
            }
        });
    }
}
