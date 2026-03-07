package br.com.orquestrator.orquestrator.core.engine.runtime;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SignalRegistry {

    private final Map<String, CompletableFuture<Void>> channels = new ConcurrentHashMap<>();

    public void emit(String signalName) {
        getChannel(signalName).complete(null);
    }

    /**
     * Propaga uma falha para todos os dependentes deste sinal.
     */
    public void fail(String signalName, Throwable cause) {
        getChannel(signalName).completeExceptionally(cause);
    }

    public void await(String signalName) {
        try {
            // Aguarda o sinal. Se o pai falhar, o 'get' lança ExecutionException imediatamente.
            getChannel(signalName).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Dependência não satisfeita: " + signalName, e);
        }
    }

    private CompletableFuture<Void> getChannel(String signalName) {
        return channels.computeIfAbsent(signalName, k -> new CompletableFuture<>());
    }
}
