package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TagManager {

    private final List<TagProvider> providers;

    public void resolveAndApply(ReadableContext reader, WriteableContext writer) {
        Set<String> allTags = new HashSet<>();
        
        for (TagProvider provider : providers) {
            allTags.addAll(provider.resolve(reader));
        }

        // Grava as tags no contexto usando DataValue
        writer.put(ContextSchema.tags(), DataValue.of(allTags));
        
        // Também registra individualmente no writer para controle de fluxo
        allTags.forEach(writer::addTag);
    }
}
