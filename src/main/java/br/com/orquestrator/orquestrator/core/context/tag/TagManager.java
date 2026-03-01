package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TagManager: Responsável por gerenciar a identificação de cenários (tags) no contexto.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TagManager {

    private final List<TagProvider> providers;

    /**
     * Resolve e adiciona todas as tags aplicáveis ao contexto.
     */
    public void resolveAndApply(ExecutionContext context) {
        for (var provider : providers) {
            try {
                var tags = provider.resolveTags(context);
                if (tags != null) {
                    tags.forEach(context::addTag);
                }
            } catch (Exception e) {
                log.error("Erro ao resolver tags via provider {}: {}", provider.getClass().getSimpleName(), e.getMessage());
            }
        }
        log.debug("Tags finais para o contexto [{}]: {}", context.getCorrelationId(), context.getTags());
    }
}
