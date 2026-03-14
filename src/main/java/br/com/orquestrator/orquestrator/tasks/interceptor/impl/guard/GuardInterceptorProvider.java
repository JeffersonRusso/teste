package br.com.orquestrator.orquestrator.tasks.interceptor.impl.guard;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import org.springframework.stereotype.Component;

@Component
public class GuardInterceptorProvider extends AbstractInterceptorProvider<GuardConfig> {

    private final ExpressionEngine expressionEngine;

    public GuardInterceptorProvider(TaskBindingResolver bindingResolver, ExpressionEngine expressionEngine) {
        super(bindingResolver, GuardConfig.class);
        this.expressionEngine = expressionEngine;
    }

    @Override public String getType() { return "guard"; }
    @Override public int getOrder() { return 100; } // Guarda protege a execução da task

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<GuardConfig> config, TaskDefinition taskDef) {
        return new GuardInterceptor(expressionEngine, config);
    }
}
