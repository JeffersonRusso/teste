package br.com.orquestrator.orquestrator.tasks.interceptor.impl.semantic;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.engine.semantic.SemanticRegistry;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("SEMANTIC")
public class SemanticInterceptorProvider extends AbstractInterceptorProvider<Map<String, Object>> {

    private final SemanticRegistry semanticRegistry;
    private final DataFactory dataFactory;

    public SemanticInterceptorProvider(TaskBindingResolver bindingResolver, 
                                       SemanticRegistry semanticRegistry,
                                       DataFactory dataFactory) {
        super(bindingResolver, (Class<Map<String, Object>>) (Class<?>) Map.class);
        this.semanticRegistry = semanticRegistry;
        this.dataFactory = dataFactory;
    }

    @Override public int getOrder() { return 30; }
    @Override public boolean isGlobal() { return true; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<Map<String, Object>> config, TaskDefinition taskDef) {
        return new SemanticInterceptor(semanticRegistry, dataFactory);
    }
}
