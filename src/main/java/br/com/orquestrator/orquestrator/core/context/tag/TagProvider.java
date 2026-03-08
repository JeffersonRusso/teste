package br.com.orquestrator.orquestrator.core.context.tag;

import java.util.Map;
import java.util.Set;

/**
 * TagProvider: Interface para provedores de tags dinâmicas.
 * Recebe o contexto bruto da requisição (headers e body).
 */
public interface TagProvider {
    Set<String> resolve(Map<String, String> headers, Map<String, Object> body);
}
