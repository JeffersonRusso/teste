package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
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
        MarshallingPlan plan = context.marshaller().createPlan(def);

        // DESACOPLAMENTO TOTAL: O compilador pergunta ao registro qual é a config
        Class<?> configClass = taskRegistry.getConfigClass(def.type()).orElse(null);

        List<TaskDecorator> chain = new DecoratorPipelineBuilder(context, def)
                .withInfra()
                .withData(plan)
                .withConfigResolution(configClass)
                .withOutput(plan)
                .withFeatures()
                .withGuard()
                .withValidation()
                .build();

        return assemble(core, chain);
    }

    private Task assemble(Task core, List<TaskDecorator> chain) {
        Task current = core;
        for (int i = chain.size() - 1; i >= 0; i--) {
            final TaskDecorator decorator = chain.get(i);
            final Task next = current;
            current = (ctx) -> decorator.apply(ctx, next::execute);
        }
        return current;
    }
}
