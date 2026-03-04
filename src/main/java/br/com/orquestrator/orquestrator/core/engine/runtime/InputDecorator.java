package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class InputDecorator implements TaskDecorator {
    private final DataMarshaller marshaller;
    private final MarshallingPlan plan;

    @Override
    public TaskResult apply(TaskChain next) {
        // Executa o plano pré-compilado
        Map<String, Object> taskInputs = marshaller.resolveInputs(plan, ContextHolder.reader());
        return ScopedValue.where(ContextHolder.CURRENT_INPUTS, taskInputs).get(next::proceed);
    }
}
