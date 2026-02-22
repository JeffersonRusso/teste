package br.com.orquestrator.orquestrator.core.context.init;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

/**
 * Contrato para inicializadores de contexto.
 */
public interface ContextInitializer {
    void initialize(ExecutionContext context);
}
