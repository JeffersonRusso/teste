package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * ValidationDecorator: Aplica validações de contrato e de tarefa antes da execução.
 */
@RequiredArgsConstructor
public class ValidationDecorator implements TaskInterceptor {
    private final TaskValidator validator;
    private final DataValidator dataValidator;
    private final ContractRegistry contractRegistry;
    private final TaskDefinition definition;

    @Override
    public TaskResult intercept(Chain chain) {
        Map<String, JsonNode> taskInputs = chain.inputs();
        
        Map<String, Object> rawInputs = new HashMap<>();
        taskInputs.forEach((k, v) -> rawInputs.put(k, v));
        
        validator.validate(definition, rawInputs);
        validateDataContracts(taskInputs);
        
        return chain.proceed(taskInputs);
    }

    private void validateDataContracts(Map<String, JsonNode> inputs) {
        definition.inputs().forEach((localKey, binding) -> {
            contractRegistry.get(binding.signalName()).ifPresent(contract -> {
                dataValidator.validate(contract, inputs.getOrDefault(localKey, MissingNode.getInstance()));
            });
        });
    }
}
