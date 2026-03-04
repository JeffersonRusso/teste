package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import java.util.Set;

/**
 * TagProvider: Contrato para resolvedores de cenários dinâmicos.
 */
public interface TagProvider {
    /**
     * Resolve tags baseadas na visão de leitura do contexto.
     */
    Set<String> resolveTags(ReadableContext context);

    /**
     * Define a ordem de execução (Menor valor = maior prioridade).
     */
    default int getPriority() { return 100; }
}
