package br.com.orquestrator.orquestrator.core.context.tag;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SystemTagProvider: Resolve tags automáticas do sistema baseadas no contexto da requisição.
 */
@Component
public class SystemTagProvider implements TagProvider {

    @Override
    public Set<String> resolve(Map<String, String> headers, Map<String, Object> body) {
        Set<String> tags = new HashSet<>();
        
        // Exemplo: Adiciona tag baseada no ambiente se fornecido via header
        if (headers != null && headers.containsKey("X-Environment")) {
            tags.add(headers.get("X-Environment").toLowerCase());
        }
        
        return tags;
    }
}
