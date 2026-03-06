package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import java.util.Set;

public interface TagProvider {
    /** Retorna o conjunto de tags identificadas para o contexto atual. */
    Set<String> resolve(ReadableContext reader);
}
