package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * SignalRegistry: Gerenciador de canais de sinais (Dataflow).
 * Blindado para alta concorrência e Virtual Threads.
 */
@Slf4j
public class SignalRegistry {

    private final Map<String, CompletableFuture<DataValue>> channels = new ConcurrentHashMap<>(64);

    /**
     * Emite um sinal de forma atômica.
     */
    public void emit(String signalName, DataValue value) {
        CompletableFuture<DataValue> channel = getChannel(signalName);
        if (channel.isDone()) {
            log.warn("Sinal [{}] já foi emitido anteriormente. Ignorando segunda emissão.", signalName);
            return;
        }
        log.debug("Emitindo sinal: {} com valor: {}", signalName, value);
        channel.complete(value != null ? value : DataValue.EMPTY);
    }

    /**
     * Propaga uma falha para o canal de forma segura.
     */
    public void fail(String signalName, Throwable cause) {
        log.error("Falha no sinal {}: {}", signalName, cause.getMessage());
        getChannel(signalName).completeExceptionally(cause);
    }

    /**
     * Aguarda um sinal respeitando um timeout de segurança.
     */
    public DataValue await(String signalName) {
        try {
            // Timeout longo para o await individual, o motor controla o timeout global
            return getChannel(signalName).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Dependência não satisfeita para o sinal {}: {}", signalName, e.getMessage());
            throw new RuntimeException("Falha ao obter dado do sinal: " + signalName, e);
        }
    }

    /**
     * Tira um snapshot thread-safe do estado atual dos sinais.
     */
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
        // computeIfAbsent é atômico no ConcurrentHashMap
        return channels.computeIfAbsent(signalName, k -> new CompletableFuture<>());
    }
}
