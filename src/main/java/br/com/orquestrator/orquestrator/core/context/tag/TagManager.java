package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * TagManager: Orquestra a resolução e aplicação de tags de cenário.
 * Segue o padrão Strategy ordenado.
 */
@Slf4j
@Component
public class TagManager {

    private final List<TagProvider> providers;

    public TagManager(List<TagProvider> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt(TagProvider::getPriority))
                .toList();
    }

    /**
     * Resolve e aplica tags usando as visões restritas.
     */
    public void resolveAndApply(ReadableContext reader, WriteableContext writer) {
        for (var provider : providers) {
            try {
                var tags = provider.resolveTags(reader);
                if (tags != null && !tags.isEmpty()) {
                    tags.forEach(writer::addTag);
                }
            } catch (Exception e) {
                log.error("Falha ao resolver tags via {}: {}", provider.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
