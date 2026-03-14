package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.cache.CacheEngine;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CacheInterceptorProvider extends AbstractInterceptorProvider<CacheConfig> {

    private final ExpressionEngine expressionEngine;
    private final CacheEngine cacheEngine;
    private final DataFactory dataFactory;
    private final ObjectMapper objectMapper;

    public CacheInterceptorProvider(TaskBindingResolver bindingResolver, 
                                    ExpressionEngine expressionEngine, 
                                    CacheEngine cacheEngine,
                                    DataFactory dataFactory,
                                    ObjectMapper objectMapper) {
        super(bindingResolver, CacheConfig.class);
        this.expressionEngine = expressionEngine;
        this.cacheEngine = cacheEngine;
        this.dataFactory = dataFactory;
        this.objectMapper = objectMapper;
    }

    @Override public String getType() { return "cache"; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<CacheConfig> config, TaskDefinition taskDef) {
        return new CacheInterceptor(expressionEngine, cacheEngine, config, dataFactory, objectMapper);
    }
}
