package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorTask;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskChainCompiler {

    private final CompilationContext context;
    private final TaskRegistry taskRegistry;

    public Task compile(Task core, TaskDefinition def) {
        MarshallingPlan plan = context.outputCompiler().createPlan(def);
        Class<?> configClass = taskRegistry.getConfigClass(def.type()).orElse(null);

        // O Builder agora retorna uma lista de Interceptores (Bolinhas lineares)
        List<TaskInterceptor> interceptors = new DecoratorPipelineBuilder(context, def)
                .withInfra()
                .withData(plan)
                .withConfigResolution(configClass)
                .withOutput(plan)
                .withFeatures()
                .withGuard()
                .withValidation()
                .buildInterceptors();

        // Retorna a task envolta na cadeia linear
        return new InterceptorTask(core, interceptors);
    }
}
