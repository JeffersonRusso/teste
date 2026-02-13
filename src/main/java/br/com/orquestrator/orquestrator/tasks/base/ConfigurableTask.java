package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.exception.PipelineException;

/**
 * Contrato para tasks que possuem configuração externa.
 * Garante que a task valide seus parâmetros antes da execução (Fail-Fast).
 */
public interface ConfigurableTask {

    /**
     * Valida se a configuração fornecida (JSON) contém todos os campos obrigatórios.
     * Deve lançar exceção se a configuração for inválida.
     *
     * @throws PipelineException ou IllegalArgumentException se a configuração for inválida.
     */
    void validateConfig();
}
