package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ResponseValidatorInterceptor: Governança de contrato atualizada para o novo domínio.
 */
@Slf4j
@RequiredArgsConstructor
public final class ResponseValidatorInterceptor implements TaskInterceptor {

    private final ContractRegistry contractRegistry;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        TaskDefinition definition = context.getDefinition();
        TaskResult result = chain.proceed(context);

        if (result instanceof TaskResult.Success s) {
            validateOutputs(definition, s.body());
        }

        return result;
    }

    private void validateOutputs(TaskDefinition definition, DataNode resultBody) {
        // CORREÇÃO: Usa a lista de outputs oficial da definição rica
        definition.outputs().forEach(output -> {
            contractRegistry.get(output.targetSignal()).ifPresent(contract -> {
                // Navega no resultado usando a chave local e valida
                DataNode valueToValidate = resultBody.get(output.localKey());
                try {
                    contract.validate(valueToValidate);
                    log.debug("Sinal [{}] validado com sucesso.", output.targetSignal());
                } catch (Exception e) {
                    log.error("Falha de contrato no sinal [{}]: {}", output.targetSignal(), e.getMessage());
                    throw new RuntimeException("Violação de Contrato: " + e.getMessage());
                }
            });
        });
    }
}
