package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class LegacyTagProvider implements TagProvider {

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> resolve(ReadableContext reader) {
        Set<String> tags = new HashSet<>();
        
        // O get() agora retorna DataValue, usamos getRaw() para compatibilidade legada
        Object headers = reader.getRaw(ContextKey.HEADER);
        if (headers instanceof Map) {
            Map<String, String> headerMap = (Map<String, String>) headers;
            if (headerMap.containsKey("X-Legacy-Mode")) {
                tags.add("legacy");
            }
        }
        
        return tags;
    }
}
