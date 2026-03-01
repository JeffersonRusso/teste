package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;

import java.util.Optional;

/**
 * ContextHolder: Gerencia o escopo soberano do request usando ScopedValue (Java 21).
 */
public class ContextHolder {

    public static final ScopedValue<ExecutionContext> CONTEXT = ScopedValue.newInstance();
    public static final ScopedValue<String> CURRENT_NODE = ScopedValue.newInstance();
    
    // NOVO: Escopo para o contexto de avaliação SpEL
    public static final ScopedValue<EvaluationContext> EVAL_CONTEXT = ScopedValue.newInstance();

    public static Optional<ExecutionContext> getContext() {
        return CONTEXT.isBound() ? Optional.of(CONTEXT.get()) : Optional.empty();
    }
}
