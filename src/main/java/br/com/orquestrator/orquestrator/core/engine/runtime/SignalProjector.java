package br.com.orquestrator.orquestrator.core.engine.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public interface SignalProjector {
    Map<String, JsonNode> projectIn(SignalRegistry signals);
    void projectOut(JsonNode result, SignalRegistry signals);
    void fail(SignalRegistry signals, Throwable cause);
}
