package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * ValidationDecorator: Aplica validações de contrato e de tarefa antes da execução.
 * Agora adaptado para o Shadow Context (DataValue).
 */
@RequiredArgsConstructor
public class ValidationDecorator implements TaskInterceptor {
    private final TaskValidator validator;
    private final DataValidator dataValidator;
    private final ContractRegistry contractRegistry;
    private final TaskDefinition definition;

    @Override
    public TaskResult intercept(Chain chain) {
        Map<String, DataValue> taskInputs = chain.context().inputs();
        
        // 1. Converte o Shadow Context (DataValue) para Raw Map (Object) para os validadores
        Map<String, Object> rawInputs = new HashMap<>();
        taskInputs.forEach((k, v) -> rawInputs.put(k, v.raw()));
        
        // 2. Executa as validações
        validator.validate(definition, rawInputs);
        validateDataContracts(taskInputs);
        
        return chain.proceed(chain.context());
    }

    private void validateDataContracts(Map<String, DataValue> inputs) {
        definition.inputs().forEach((localKey, globalKey) -> {
            contractRegistry.get(globalKey).ifPresent(contract -> {
                // O DataValidator recebe o valor bruto (raw) do DataValue
                dataValidator.validate(contract, inputs.getOrDefault(localKey, DataValue.EMPTY).raw());
            });
        });
    }
}
