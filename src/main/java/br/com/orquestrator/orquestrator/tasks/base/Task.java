package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

/**
 * Unidade de trabalho funcional com contrato de saída definido.
 */
@FunctionalInterface
public interface Task {
    /**
     * Executa a lógica e retorna um resultado padronizado.
     */
    TaskResult execute(ExecutionContext context);
}
