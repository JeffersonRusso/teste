package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutputMappingDecorator implements TaskDecorator {
    private final DataMarshaller marshaller;
    private final MarshallingPlan plan;

    @Override
    public TaskResult apply(TaskChain next) {
        TaskResult result = next.proceed();
        if (result != null) {
            // Executa o plano pré-compilado
            marshaller.mapOutputs(plan, result, ContextHolder.writer());
        }
        return result;
    }
}
