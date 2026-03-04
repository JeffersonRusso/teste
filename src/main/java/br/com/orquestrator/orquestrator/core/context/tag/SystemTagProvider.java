package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * SystemTagProvider: Garante que tags de infraestrutura e metadados sejam aplicadas.
 */
@Component
public class SystemTagProvider implements TagProvider {

    @Override
    public Set<String> resolveTags(ReadableContext context) {
        // Exemplo: Adiciona a tag da operação como tag de cenário
        Object opType = context.get(ContextKey.OPERATION_TYPE);
        if (opType != null) {
            return Set.of(opType.toString().toLowerCase());
        }
        return Set.of();
    }

    @Override public int getPriority() { return 0; } // Roda primeiro
}
