package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.springframework.expression.EvaluationContext;

import java.util.Map;
import java.util.Optional;

/**
 * ContextHolder: Acesso estático ao contexto da thread (ScopedValue).
 * Minimalista e direto.
 */
public class ContextHolder {

    public static final ScopedValue<ExecutionContext> CONTEXT = ScopedValue.newInstance();
    public static final ScopedValue<EvaluationContext> EVAL_CONTEXT = ScopedValue.newInstance();
    public static final ScopedValue<Map<String, Object>> CURRENT_INPUTS = ScopedValue.newInstance();
    public static final ScopedValue<String> CURRENT_NODE = ScopedValue.newInstance();

    // Atalhos de conveniência (apenas leitura)
    public static ReadableContext reader() { return get(); }
    public static WriteableContext writer() { return get(); }
    public static ContextMetadata metadata() { return get(); }

    /**
     * Retorna o contexto atual ou lança exceção se não estiver preso.
     * Use quando você tem certeza que está dentro de uma sessão.
     */
    private static ExecutionContext get() {
        if (!CONTEXT.isBound()) {
            throw new PipelineException("Contexto não encontrado. Você está rodando fora de uma ExecutionSession?");
        }
        return CONTEXT.get();
    }

    /**
     * Retorna o contexto atual de forma segura (Optional).
     * Use em locais onde o contexto pode não existir (ex: ExceptionHandler, Logs).
     */
    public static Optional<ExecutionContext> getContext() {
        return CONTEXT.isBound() ? Optional.of(CONTEXT.get()) : Optional.empty();
    }
}
