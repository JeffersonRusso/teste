package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GuardExecutionNode implements ExecutionNode {

    private final ExecutionNode delegate;
    private final String condition;
    private final ExpressionEngine expressionEngine;

    @Override
    public void run(SignalRegistry signals) {
        delegate.onSignal(signals);

        if (shouldExecute()) {
            delegate.run(signals);
        } else {
            log.debug("Nó [{}] pulado pela condição de guarda: {}", nodeId(), condition);
            delegate.emitSignal(signals);
        }
    }

    private boolean shouldExecute() {
        if (condition == null || condition.isBlank()) return true;
        // OTIMIZAÇÃO: Compila e avalia
        return Boolean.TRUE.equals(expressionEngine.compile(condition).evaluate(ContextHolder.reader(), Boolean.class));
    }

    @Override public void onSignal(SignalRegistry signals) { delegate.onSignal(signals); }
    @Override public void emitSignal(SignalRegistry signals) { delegate.emitSignal(signals); }
    @Override public String nodeId() { return delegate.nodeId(); }
}
