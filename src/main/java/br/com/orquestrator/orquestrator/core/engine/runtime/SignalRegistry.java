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

    public void await(String signalName) {
        try {
            // Aguarda o sinal com um timeout de segurança para evitar deadlocks infinitos
            getChannel(signalName).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Timeout aguardando sinal: {}. Possível quebra no grafo de dependências.", signalName);
            throw new RuntimeException("Sinal não recebido: " + signalName, e);
        }
    }

    private CompletableFuture<Void> getChannel(String signalName) {
        return channels.computeIfAbsent(signalName, k -> new CompletableFuture<>());
    }
}
