package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
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
    private final TaskDefinition definition;

    @Override
    public TaskResult apply(TaskChain next) {
        // Recupera os inputs do escopo
        Map<String, Object> taskInputs = ContextHolder.CURRENT_INPUTS.get();
        validator.validate(definition, taskInputs);
        return next.proceed();
    }
}
