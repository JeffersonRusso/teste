package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * SignalRegistry: Gerenciador de canais de sinais (Dataflow).
 * Instrumentado para diagnóstico de latência.
 */
@Slf4j
public class SignalRegistry {

    private final Map<String, CompletableFuture<DataValue>> channels = new ConcurrentHashMap<>(64);

    public void emit(String signalName, DataValue value) {
        CompletableFuture<DataValue> channel = getChannel(signalName);
        if (channel.isDone()) {
            return;
        }
        // log.error("[PROFILER] SignalRegistry - EMIT: {} | TS: {}", signalName, System.nanoTime());
        channel.complete(value != null ? value : DataValue.EMPTY);
    }

    public void fail(String signalName, Throwable cause) {
        log.error("Falha no sinal {}: {}", signalName, cause.getMessage());
        getChannel(signalName).completeExceptionally(cause);
    }

    public DataValue await(String signalName) {
        long start = System.nanoTime();
        try {
            DataValue result = getChannel(signalName).get(60, TimeUnit.SECONDS);
            long duration = System.nanoTime() - start;
            
            // Loga apenas se o tempo de espera for significativo (> 5ms) para não poluir demais
            if (duration > 5_000_000) {
                log.error("[PROFILER] SignalRegistry - AWAIT: {} | Waited: {}ms", signalName, duration / 1_000_000.0);
            }
            return result;
        } catch (Exception e) {
            log.error("Dependência não satisfeita para o sinal {}: {}", signalName, e.getMessage());
            throw new RuntimeException("Falha ao obter dado do sinal: " + signalName, e);
        }
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> photo = new ConcurrentHashMap<>();
        channels.forEach((name, future) -> {
            if (future.isDone() && !future.isCompletedExceptionally()) {
                DataValue val = future.join();
                if (val != null) photo.put(name, val.raw());
            }
        });
        return photo;
    }

    private CompletableFuture<DataValue> getChannel(String signalName) {
        return channels.computeIfAbsent(signalName, k -> new CompletableFuture<>());
    }
}
