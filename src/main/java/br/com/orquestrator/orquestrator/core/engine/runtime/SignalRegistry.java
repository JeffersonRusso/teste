package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.api.signal.Signal;
import br.com.orquestrator.orquestrator.api.signal.SignalResult;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * SignalRegistry: Repositório de estado de sinais (DataNodes) durante a execução.
 * Implementação purificada e thread-safe.
 */
@Slf4j
public class SignalRegistry {

    private final Map<String, CompletableFuture<SignalResult>> channels = new ConcurrentHashMap<>(64);

    public void emit(Signal signal, DataNode value) {
        CompletableFuture<SignalResult> channel = getChannel(signal.signalName());
        if (channel.isDone()) return;
        
        SignalResult result = (value == null || value.isMissing()) 
                ? new SignalResult.Empty() 
                : new SignalResult.Present(value);
                
        channel.complete(result);
    }

    public void emitAll(Map<? extends Signal, ? extends DataNode> signals) {
        if (signals != null) {
            signals.forEach(this::emit);
        }
    }

    public void fail(Signal signal, Throwable cause) {
        log.error("Falha no sinal {}: {}", signal.signalName(), cause.getMessage());
        getChannel(signal.signalName()).complete(new SignalResult.Failed(cause));
    }

    public SignalResult await(Signal signal) {
        try {
            return getChannel(signal.signalName()).get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Timeout ou erro ao aguardar o sinal {}: {}", signal.signalName(), e.getMessage());
            return new SignalResult.Failed(e);
        }
    }

    private CompletableFuture<SignalResult> getChannel(String signalName) {
        return channels.computeIfAbsent(signalName, k -> new CompletableFuture<>());
    }
}
