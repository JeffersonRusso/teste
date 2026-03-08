package br.com.orquestrator.orquestrator.core.context.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TagManager: Gerencia a resolução de tags dinâmicas para a requisição.
 * As tags são usadas para selecionar a versão correta do pipeline (Grafo).
 */
@Component
@RequiredArgsConstructor
public class TagManager {

    private final List<TagProvider> providers;

    /**
     * Resolve todas as tags aplicáveis à requisição (headers e body).
     */
    public Set<String> resolve(Map<String, String> headers, Map<String, Object> body) {
        Set<String> allTags = new HashSet<>();
        allTags.add("default");
        
        if (providers != null) {
            for (TagProvider provider : providers) {
                allTags.addAll(provider.resolve(headers, body));
            }
        }

        return allTags;
    }
}
