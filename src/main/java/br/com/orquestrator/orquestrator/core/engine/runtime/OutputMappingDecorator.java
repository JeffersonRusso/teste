package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutputMappingDecorator implements TaskDecorator {

    private final DataMarshaller marshaller;
    private final DataValidator dataValidator;
    private final ContractRegistry contractRegistry;
    private final MarshallingPlan plan;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        TaskResult result = next.proceed(context);
        
        if (result.isSuccess()) {
            // 1. Validação de Saída contra os Contratos
            validateOutputs(result);
            
            // 2. Mapeamento e Etiquetagem Semântica
            marshaller.mapOutputs(plan, result, ContextHolder.writer());
        }
        
        return result;
    }

    private void validateOutputs(TaskResult result) {
        // Corrigido: Usando outputPlan() em vez de outputMap()
        plan.outputPlan().forEach((expression, targetPath) -> {
            contractRegistry.get(targetPath.value()).ifPresent(compiledContract -> {
                dataValidator.validate(compiledContract, result.body().raw());
            });
        });
    }
}
