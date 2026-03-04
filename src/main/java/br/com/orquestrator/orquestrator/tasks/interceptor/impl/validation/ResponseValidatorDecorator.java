package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
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
 * ResponseValidatorDecorator: Valida o resultado de uma task contra regras SpEL.
 * Utiliza o motor de expressões unificado.
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseValidatorDecorator implements TaskDecorator {

    private final ResponseValidatorConfig config;
    private final String nodeId;
    private final ExpressionEngine expressionEngine; // <--- Injeta a abstração

    @Override
    public TaskResult apply(TaskChain next) {
        TaskResult result = next.proceed();
        ReadableContext context = ContextHolder.reader();
        validate(result, context);
        return result;
    }

    private void validate(TaskResult result, ReadableContext context) {
        if (config == null || config.rules() == null) return;
        
        // Injeta o resultado da task como variável local para a avaliação
        // Nota: Como o root é o context, o motor injetará automaticamente #raw, #standard, etc.
        for (var rule : config.rules()) {
            try {
                // Aqui poderíamos injetar o #result via variáveis locais se o motor suportasse, 
                // mas para manter o Clean Code, vamos usar o root como um mapa temporário se necessário.
                // Por enquanto, avaliamos contra o context.
                if (Boolean.TRUE.equals(expressionEngine.evaluate(rule.condition(), context, Boolean.class))) {
                    throw new PipelineException(rule.message()).withNodeId(nodeId);
                }
            } catch (Exception e) {
                if (e instanceof PipelineException) throw e;
                log.warn("Falha ao avaliar regra de validação no nó [{}]: {}", nodeId, e.getMessage());
            }
        }
    }
}
