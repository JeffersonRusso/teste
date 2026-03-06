package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.domain.vo.PipelineIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PipelineRegistry: Gerencia o cache de pipelines compilados.
 * Otimizado com PipelineIdentity para lookup O(1) de alta performance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineRegistry {

    private final PipelineCompiler compiler;
    private final Map<PipelineIdentity, Pipeline> cache = new ConcurrentHashMap<>(128);

    /**
     * Recupera um pipeline do cache ou compila um novo se necessário.
     */
    public Pipeline get(PipelineDefinition def, Set<String> activeTags) {
        PipelineIdentity identity = new PipelineIdentity(def.operationType(), def.version(), activeTags);
        
        return cache.computeIfAbsent(identity, key -> {
            log.info("Compilando novo pipeline executável para: {} | Tags: {}", def.operationType(), activeTags);
            return compiler.compile(def, activeTags);
        });
    }

    public void clear() {
        cache.clear();
        log.info("Cache de pipelines executáveis limpo.");
    }
}
