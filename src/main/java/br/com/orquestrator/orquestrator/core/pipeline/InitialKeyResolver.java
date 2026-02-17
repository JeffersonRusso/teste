package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.cache.GlobalDataCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * InitialKeyResolver: Especialista em identificar as chaves disponíveis no início do pipeline.
 */
@Component
@RequiredArgsConstructor
public class InitialKeyResolver {

    private final GlobalDataCache globalCache;
    private final TaskCatalogProvider taskProvider;

    public Set<String> resolve(ExecutionContext context) {
        Set<String> keys = new HashSet<>(context.getRoot().keySet());
        keys.addAll(globalCache.getAll().keySet());
        
        // Adiciona chaves de tasks globais
        taskProvider.findAllActive().stream()
                .filter(TaskDefinition::isGlobal)
                .forEach(t -> {
                    keys.add(t.getNodeId().value());
                    if (t.getProduces() != null) t.getProduces().forEach(p -> keys.add(p.name()));
                });
        
        return keys;
    }
}
