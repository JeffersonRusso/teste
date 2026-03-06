package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GuardExecutionNode: Decorador de nó que implementa a lógica de execução condicional.
 * SOLID: Single Responsibility Principle (O motor não precisa saber de guards).
 */
@Slf4j
@RequiredArgsConstructor
public class GuardExecutionNode implements ExecutionNode {

    private final ExecutionNode delegate;
    private final String condition;
    private final ExpressionEngine expressionEngine;

    @Override
    public void run(SignalRegistry signals) {
        // 1. O GuardNode SEMPRE deve participar da malha de sinais para não travar o grafo
        // Mas ele delega a espera para o nó interno ou faz aqui?
        // Melhor: O GuardNode espera os sinais ANTES de avaliar a condição.
        
        delegate.onSignal(signals);

        // 2. Avalia a condição
        if (shouldExecute()) {
            delegate.run(signals); // Executa o nó real (que já teve onSignal chamado)
        } else {
            log.debug("Nó [{}] pulado pela condição de guarda: {}", nodeId(), condition);
            delegate.emitSignal(signals); // Emite os sinais para não travar os sucessores
        }
    }

    private boolean shouldExecute() {
        if (condition == null || condition.isBlank()) return true;
        return Boolean.TRUE.equals(expressionEngine.evaluate(condition, ContextHolder.reader(), Boolean.class));
    }

    @Override public void onSignal(SignalRegistry signals) { delegate.onSignal(signals); }
    @Override public void emitSignal(SignalRegistry signals) { delegate.emitSignal(signals); }
    @Override public String nodeId() { return delegate.nodeId(); }
}
