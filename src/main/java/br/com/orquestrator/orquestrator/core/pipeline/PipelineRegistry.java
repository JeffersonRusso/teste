package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PipelineRegistry: Gerencia o cache de pipelines compilados.
 * Garante que a compilação pesada ocorra apenas quando necessário.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineRegistry {

    private final PipelineCompiler compiler;
    private final Map<String, Pipeline> cache = new ConcurrentHashMap<>(128);

    /**
     * Recupera um pipeline do cache ou compila um novo se necessário.
     * A chave do cache considera a operação e as tags ativas.
     */
    public Pipeline get(PipelineDefinition def, Set<String> activeTags) {
        String cacheKey = generateKey(def, activeTags);
        
        return cache.computeIfAbsent(cacheKey, key -> {
            log.info("Compilando novo pipeline executável para: {} | Tags: {}", def.operationType(), activeTags);
            return compiler.compile(def, activeTags);
        });
    }

    private String generateKey(PipelineDefinition def, Set<String> tags) {
        // Chave composta: Operação + Versão + Tags (ordenadas para consistência)
        return String.format("%s:v%d:%s", 
            def.operationType(), 
            def.version(), 
            tags.stream().sorted().toList());
    }

    /** Limpa o cache (útil para hot-reload). */
    public void clear() {
        cache.clear();
        log.info("Cache de pipelines executáveis limpo.");
    }
}
