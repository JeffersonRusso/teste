package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ValidationDecorator implements TaskDecorator {
    private final TaskValidator validator;
    private final DataValidator dataValidator; // <--- NOVO
    private final ContractRegistry contractRegistry; // <--- NOVO
    private final TaskDefinition definition;

    @Override
    public TaskResult apply(TaskChain next) {
        Map<String, Object> taskInputs = ContextHolder.CURRENT_INPUTS.get();
        
        // 1. Validação de Contrato de Task (Obrigatoriedade simples)
        validator.validate(definition, taskInputs);
        
        // 2. Validação de Contrato de Dados (RG dos Dados)
        validateDataContracts(taskInputs);
        
        return next.proceed();
    }

    private void validateDataContracts(Map<String, Object> inputs) {
        definition.inputs().forEach((localKey, globalKey) -> {
            contractRegistry.get(globalKey).ifPresent(contract -> {
                dataValidator.validate(contract, inputs.get(localKey));
            });
        });
    }
}
