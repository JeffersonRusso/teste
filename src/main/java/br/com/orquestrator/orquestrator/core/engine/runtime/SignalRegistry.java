package br.com.orquestrator.orquestrator.core.engine.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * SignalRegistry: Gerenciador de canais de sinais.
 * Agora gerencia JsonNode diretamente.
 */
@Slf4j
public class SignalRegistry {

    private final Map<String, CompletableFuture<JsonNode>> channels = new ConcurrentHashMap<>(64);

    public void emit(String signalName, JsonNode value) {
        CompletableFuture<JsonNode> channel = getChannel(signalName);
        if (channel.isDone()) return;
        channel.complete(value != null ? value : MissingNode.getInstance());
    }

    public void fail(String signalName, Throwable cause) {
        log.error("Falha no sinal {}: {}", signalName, cause.getMessage());
        getChannel(signalName).completeExceptionally(cause);
    }

    public JsonNode await(String signalName) {
        try {
            return getChannel(signalName).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Dependência não satisfeita para o sinal {}: {}", signalName, e.getMessage());
            throw new RuntimeException("Falha ao obter dado do sinal: " + signalName, e);
        }
    }

    public JsonNode get(String path) {
        if (path == null || path.isBlank()) return MissingNode.getInstance();

        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        int slashIndex = cleanPath.indexOf('/');

        String signalName = slashIndex == -1 ? cleanPath : cleanPath.substring(0, slashIndex);
        
        JsonNode rootValue = await(signalName);

        if (slashIndex == -1) return rootValue;

        String subPath = "/" + cleanPath.substring(slashIndex + 1);
        return rootValue.at(subPath);
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> photo = new ConcurrentHashMap<>();
        channels.forEach((name, future) -> {
            if (future.isDone() && !future.isCompletedExceptionally()) {
                JsonNode val = future.join();
                if (val != null && !val.isMissingNode()) photo.put(name, val);
            }
        });
        return photo;
    }

    private CompletableFuture<JsonNode> getChannel(String signalName) {
        return channels.computeIfAbsent(signalName, k -> new CompletableFuture<>());
    }
}
