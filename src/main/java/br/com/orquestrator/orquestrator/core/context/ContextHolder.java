package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import java.util.Map;
import java.util.Optional;

/**
 * ContextHolder: O ponto único de soberania do request.
 */
public class ContextHolder {

    public static final ScopedValue<ExecutionContext> CONTEXT = ScopedValue.newInstance();
    public static final ScopedValue<EvaluationContext> EVAL_CONTEXT = ScopedValue.newInstance();
    public static final ScopedValue<Map<String, Object>> CURRENT_INPUTS = ScopedValue.newInstance();
    public static final ScopedValue<String> CURRENT_NODE = ScopedValue.newInstance();

    public static ReadableContext reader() { return current(); }
    public static WriteableContext writer() { return current(); }
    public static ContextMetadata metadata() { return current(); }

    /**
     * Retorna o contexto atual ou lança exceção se não estiver preso.
     */
    public static ExecutionContext current() {
        return getContext().orElseThrow(() -> 
            new PipelineException("Acesso ao contexto negado: Fora de um escopo soberano."));
    }

    /**
     * Retorna um Optional do contexto atual.
     */
    public static Optional<ExecutionContext> getContext() {
        return CONTEXT.isBound() ? Optional.of(CONTEXT.get()) : Optional.empty();
    }
}
