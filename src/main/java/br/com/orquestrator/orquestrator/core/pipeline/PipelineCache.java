package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.health.SystemHealthMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PipelineCache: Gerencia o armazenamento de pipelines montados.
 */
@Component
@RequiredArgsConstructor
public class PipelineCache {

    private final SystemHealthMonitor healthMonitor;
    private final Map<String, Pipeline> cache = new ConcurrentHashMap<>();

    public String generateKey(String operationType, Integer flowVersion) {
        int healthScore = healthMonitor.getCutoffScore(operationType);
        return STR."\{operationType}:\{flowVersion != null ? flowVersion : 0}:\{healthScore}";
    }

    public Pipeline get(String key) {
        return cache.get(key);
    }

    public void put(String key, Pipeline pipeline) {
        cache.put(key, pipeline);
    }

    public void clear() {
        cache.clear();
    }
}
