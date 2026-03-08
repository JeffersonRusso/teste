package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * GuardExecutionNode: Decorator que aplica uma condição de guarda antes da execução do nó.
 */
@Slf4j
@RequiredArgsConstructor
public class GuardExecutionNode implements ExecutionNode {

    private final ExecutionNode delegate;
    private final String condition;
    private final ExpressionEngine expressionEngine;

    @Override
    public void run(SignalRegistry signals) {
        Map<String, DataValue> inputs = onSignal(signals);

        if (shouldExecute(inputs)) {
            delegate.run(signals);
        } else {
            log.debug("Nó [{}] pulado pela condição de guarda: {}", nodeId(), condition);
            emitSignal(signals, DataValue.EMPTY);
        }
    }

    private boolean shouldExecute(Map<String, DataValue> inputs) {
        if (condition == null || condition.isBlank()) return true;
        Map<String, Object> rawInputs = new java.util.HashMap<>();
        inputs.forEach((k, v) -> rawInputs.put(k, v.raw()));
        
        return Boolean.TRUE.equals(expressionEngine.compile(condition).evaluate(rawInputs, Boolean.class));
    }

    @Override public Map<String, DataValue> onSignal(SignalRegistry signals) { return delegate.onSignal(signals); }
    @Override public void emitSignal(SignalRegistry signals, DataValue resultBody) { delegate.emitSignal(signals, resultBody); }
    @Override public void then(ExecutionNode next) { delegate.then(next); }
    @Override public String nodeId() { return delegate.nodeId(); }
}
