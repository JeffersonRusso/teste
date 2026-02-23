package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.health.SystemHealthMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PipelineCache: Gerencia o armazenamento de pipelines montados.
 * Otimizado para evitar alocação de Strings no Hot Path.
 */
@Component
@RequiredArgsConstructor
public class PipelineCache {

    private final SystemHealthMonitor healthMonitor;
    private final Map<CacheKey, Pipeline> cache = new ConcurrentHashMap<>();

    // OTIMIZAÇÃO: Usar um record como chave de cache em vez de String
    // Records implementam equals/hashCode automaticamente e são muito rápidos.
    public record CacheKey(String operationType, int flowVersion, int healthScore) {}

    public CacheKey generateKey(String operationType, Integer flowVersion) {
        int healthScore = healthMonitor.getCutoffScore(operationType);
        return new CacheKey(operationType, flowVersion != null ? flowVersion : 0, healthScore);
    }

    public Pipeline get(CacheKey key) {
        return cache.get(key);
    }

    public void put(CacheKey key, Pipeline pipeline) {
        cache.put(key, pipeline);
    }

    public void clear() {
        cache.clear();
    }
}
