package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import java.util.Set;

/**
 * TagProvider: Contrato para componentes que identificam cenários (tags) no contexto.
 */
public interface TagProvider {
    Set<String> resolveTags(ExecutionContext context);
}
