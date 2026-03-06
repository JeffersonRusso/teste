package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class InputDecorator implements TaskDecorator {

    private final DataMarshaller marshaller;
    private final MarshallingPlan plan;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        // 1. Resolve os inputs brutos
        Map<String, Object> inputs = marshaller.resolveInputs(plan, ContextHolder.reader());
        
        // 2. Validação Semântica (Opcional, mas poderosa)
        // Aqui poderíamos iterar sobre o plano e garantir que cada input bate com o esperado.
        // Por enquanto, vamos apenas garantir que o contexto seja criado.

        TaskContext enrichedContext = new TaskContext(inputs, context.configuration(), context.nodeId());
        
        try {
            return ScopedValue.where(ContextHolder.CURRENT_INPUTS, inputs)
                    .call(() -> next.proceed(enrichedContext));
        } catch (Exception e) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
