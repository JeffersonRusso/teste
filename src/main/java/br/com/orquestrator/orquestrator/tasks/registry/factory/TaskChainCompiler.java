package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorTask;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * TaskChainCompiler: Compila a cadeia de interceptores para uma tarefa.
 * Otimizado: Resolve configurações estáticas no startup para evitar SpEL em runtime.
 */
@Component
@RequiredArgsConstructor
public class TaskChainCompiler {

    private final CompilationContext context;
    private final TaskRegistry taskRegistry;

    public Task compile(Task core, TaskDefinition def) {
        Class<?> configClass = taskRegistry.getConfigClass(def.type()).orElse(null);
        
        // OTIMIZAÇÃO: Verifica se a configuração é estática (não contém #{)
        boolean isDynamic = isConfigDynamic(def.config());
        Object staticConfig = null;
        
        if (!isDynamic && configClass != null) {
            // Resolve uma única vez no startup
            staticConfig = context.bindingResolver().resolve(def.config(), Map.of(), configClass);
        }

        // Constrói a cadeia de interceptores
        List<TaskInterceptor> interceptors = new DecoratorPipelineBuilder(context, def)
                .withInfra()
                .withData()
                .withConfigResolution(isDynamic ? configClass : null, staticConfig)
                .withFeatures()
                .withValidation()
                .buildInterceptors();

        return new InterceptorTask(core, interceptors);
    }

    private boolean isConfigDynamic(Map<String, Object> config) {
        if (config == null) return false;
        return config.values().stream()
                .anyMatch(v -> v instanceof String s && s.contains("#{"));
    }
}
