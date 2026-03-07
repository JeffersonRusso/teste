package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ValidationDecorator implements TaskInterceptor {
    private final TaskValidator validator;
    private final DataValidator dataValidator;
    private final ContractRegistry contractRegistry;
    private final TaskDefinition definition;

    @Override
    public TaskResult intercept(Chain chain) {
        Map<String, Object> taskInputs = chain.context().inputs();
        
        validator.validate(definition, taskInputs);
        validateDataContracts(taskInputs);
        
        return chain.proceed(chain.context());
    }

    private void validateDataContracts(Map<String, Object> inputs) {
        definition.inputs().forEach((localKey, globalKey) -> {
            contractRegistry.get(globalKey).ifPresent(contract -> {
                dataValidator.validate(contract, inputs.get(localKey));
            });
        });
    }
}
