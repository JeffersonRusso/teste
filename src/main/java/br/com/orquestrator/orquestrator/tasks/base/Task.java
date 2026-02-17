package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

/**
 * Unidade de trabalho funcional.
 * Recebe o contexto e retorna o resultado da sua operação.
 */
@FunctionalInterface
public interface Task {
    /**
     * Executa a lógica e retorna o dado produzido.
     */
    Object execute(ExecutionContext context);
}
