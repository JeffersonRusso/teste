package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("VALIDATION")
public class ResponseValidatorInterceptorProvider extends AbstractInterceptorProvider<Map<String, Object>> {

    private final ContractRegistry contractRegistry;
    private final ObjectMapper objectMapper;

    public ResponseValidatorInterceptorProvider(TaskBindingResolver bindingResolver, 
                                               ContractRegistry contractRegistry,
                                               ObjectMapper objectMapper) {
        super(bindingResolver, (Class<Map<String, Object>>) (Class<?>) Map.class);
        this.contractRegistry = contractRegistry;
        this.objectMapper = objectMapper;
    }

    @Override public int getOrder() { return 200; }
    @Override public boolean isGlobal() { return true; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<Map<String, Object>> config, TaskDefinition taskDef) {
        return new ResponseValidatorInterceptor(contractRegistry, objectMapper);
    }
}
