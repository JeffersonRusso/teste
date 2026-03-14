package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import java.util.List;

/**
 * Configuração para validação de resposta de tarefas.
 */
public record ResponseValidatorConfig(
    List<ValidationRule> rules
) {
    public record ValidationRule(
        String condition, // Expressão SpEL (ex: #result.body.id != null)
        String message,   // Mensagem de erro se a condição for FALSA
        String errorCode  // Código de erro para o cliente
    ) {}
}
