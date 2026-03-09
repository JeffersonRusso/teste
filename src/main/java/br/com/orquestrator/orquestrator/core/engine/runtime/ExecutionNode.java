package br.com.orquestrator.orquestrator.core.engine.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public interface ExecutionNode {
    void run(SignalRegistry signals);
    void then(ExecutionNode next);
    Map<String, JsonNode> onSignal(SignalRegistry signals);
    void emitSignal(SignalRegistry signals, JsonNode resultBody);
    String nodeId();
}
