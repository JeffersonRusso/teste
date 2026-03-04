package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.core.engine.runtime.*;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskChainCompiler {

    private final CompilationContext context;

    public Task compile(Task core, TaskDefinition def) {
        List<TaskDecorator> chain = new ArrayList<>();
        MarshallingPlan plan = context.marshaller().createPlan(def);

        // 1. Camada de Identidade e Escopo (A mais externa)
        chain.add(new ScopeDecorator(def.nodeId().value()));

        // 2. Camada de Telemetria (Mede tudo o que acontece abaixo)
        chain.add(new TelemetryDecorator(def.nodeId().value()));

        // 3. Camada de Política de Erro
        chain.add(new ErrorPolicyDecorator(def.nodeId().value(), def.failFast()));

        // 4. Camada de Marshalling
        chain.add(new InputDecorator(context.marshaller(), plan));
        chain.add(new OutputMappingDecorator(context.marshaller(), plan));

        // 5. Camada de Extensões (Features)
        chain.addAll(context.interceptorEngine().resolveInterceptors(def.features(), def.nodeId().value()));

        // 6. Camada de Validação e Guarda
        chain.add(new GuardDecorator(context.expressionEngine(), def.guardCondition(), def.nodeId().value()));
        chain.add(new ValidationDecorator(context.validator(), def));

        return assemble(core, chain);
    }

    private Task assemble(Task core, List<TaskDecorator> chain) {
        Task current = core;
        for (int i = chain.size() - 1; i >= 0; i--) {
            final TaskDecorator decorator = chain.get(i);
            final Task next = current;
            current = () -> decorator.apply(next::execute);
        }
        return current;
    }
}
