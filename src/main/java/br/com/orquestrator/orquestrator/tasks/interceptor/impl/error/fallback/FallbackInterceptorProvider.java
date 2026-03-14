package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.fallback;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

@Component
public class FallbackInterceptorProvider extends AbstractInterceptorProvider<FallbackConfig> {

    private final DataFactory dataFactory;

    public FallbackInterceptorProvider(TaskBindingResolver bindingResolver, DataFactory dataFactory) {
        super(bindingResolver, FallbackConfig.class);
        this.dataFactory = dataFactory;
    }

    @Override public String getType() { return "fallback"; }
    @Override public int getOrder() { return 150; } // Fallback deve rodar por dentro da resiliência mas por fora da task

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<FallbackConfig> config, TaskDefinition taskDef) {
        return new FallbackInterceptor(config, dataFactory);
    }
}
