package br.com.orquestrator.orquestrator.core.context.init;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

/**
 * Contrato para inicializadores de contexto.
 * Renomeado para evitar conflitos de pacote durante a refatoração.
 */
public interface ContextTaskInitializer {
    void initialize(ExecutionContext context);
}
