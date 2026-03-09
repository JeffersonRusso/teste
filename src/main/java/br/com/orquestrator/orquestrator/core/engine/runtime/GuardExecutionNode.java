package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * GuardExecutionNode: Decorator que aplica uma condição de guarda.
 * Agora usa JsonNode.
 */
@Slf4j
@RequiredArgsConstructor
public class GuardExecutionNode implements ExecutionNode {

    private final ExecutionNode delegate;
    private final String condition;
    private final ExpressionEngine expressionEngine;

    @Override
    public void run(SignalRegistry signals) {
        Map<String, JsonNode> inputs = onSignal(signals);

        if (shouldExecute(inputs)) {
            delegate.run(signals);
        } else {
            log.debug("Nó [{}] pulado pela condição de guarda: {}", nodeId(), condition);
            emitSignal(signals, MissingNode.getInstance());
        }
    }

    private boolean shouldExecute(Map<String, JsonNode> inputs) {
        if (condition == null || condition.isBlank()) return true;
        
        // O ExpressionEngine agora aceita Map<String, JsonNode> diretamente
        JsonNode result = expressionEngine.compile(condition).evaluate(inputs);
        return result.asBoolean(false);
    }

    @Override public Map<String, JsonNode> onSignal(SignalRegistry signals) { return delegate.onSignal(signals); }
    @Override public void emitSignal(SignalRegistry signals, JsonNode resultBody) { delegate.emitSignal(signals, resultBody); }
    @Override public void then(ExecutionNode next) { delegate.then(next); }
    @Override public String nodeId() { return delegate.nodeId(); }
}
