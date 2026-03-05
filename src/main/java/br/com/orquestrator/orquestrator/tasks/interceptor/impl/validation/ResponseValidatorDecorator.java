package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ResponseValidatorDecorator: Valida o corpo da resposta da task core.
 * Garante que campos obrigatórios estejam presentes antes do mapeamento.
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseValidatorDecorator implements TaskDecorator {

    private final ResponseValidatorConfig config;
    private final String nodeId;
    private final ExpressionEngine expressionEngine;

    @Override
    public TaskResult apply(TaskChain next) {
        // 1. Executa a task core (ex: HttpTask)
        TaskResult result = next.proceed();

        // 2. Valida o resultado
        if (result != null && result.isSuccess()) {
            validate(result);
        }

        return result;
    }

    private void validate(TaskResult result) {
        if (config == null || config.rules() == null) return;

        // Cria um contexto temporário com o resultado para avaliação
        Map<String, Object> evalRoot = Map.of("result", result);

        for (var rule : config.rules()) {
            // A condição deve ser VERDADEIRA para passar. Se for FALSA, lança erro.
            Boolean isValid = expressionEngine.evaluate(rule.condition(), evalRoot, Boolean.class);
            
            if (Boolean.FALSE.equals(isValid)) {
                log.error("Falha na validação de resposta do nó [{}]: {}", nodeId, rule.message());
                throw new PipelineException(rule.message())
                        .withNodeId(nodeId);
            }
        }
    }
}
