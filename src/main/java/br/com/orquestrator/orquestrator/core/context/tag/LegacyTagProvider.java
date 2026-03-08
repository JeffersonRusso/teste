package br.com.orquestrator.orquestrator.core.context.tag;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * LegacyTagProvider: Resolve a tag 'legacy' baseada em headers da requisição.
 */
@Component
public class LegacyTagProvider implements TagProvider {

    @Override
    public Set<String> resolve(Map<String, String> headers, Map<String, Object> body) {
        Set<String> tags = new HashSet<>();
        
        if (headers != null && headers.containsKey("X-Legacy-Mode")) {
            tags.add("legacy");
        }
        
        return tags;
    }
}
