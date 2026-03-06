package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SystemTagProvider implements TagProvider {

    @Override
    public Set<String> resolve(ReadableContext reader) {
        Set<String> tags = new HashSet<>();
        tags.add("default");
        
        Object opType = reader.getRaw(ContextKey.OPERATION_TYPE);
        if (opType != null) {
            tags.add(opType.toString().toLowerCase());
        }
        
        return tags;
    }
}
