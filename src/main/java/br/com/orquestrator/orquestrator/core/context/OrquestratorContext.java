package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import java.util.Set;
import java.util.function.Supplier;

/**
 * OrquestratorContext: Fachada soberana para o contexto da requisição.
 * Agora respeitando a Lei de Deméter ao expor atalhos diretos.
 */
public class OrquestratorContext {

    private static final ScopedValue<RequestIdentity> CONTEXT = ScopedValue.newInstance();

    public static <T> T runWith(RequestIdentity identity, Supplier<T> action) {
        return ScopedValue.where(CONTEXT, identity).get(action);
    }

    public static RequestIdentity get() {
        return CONTEXT.orElseThrow(() -> new IllegalStateException("Contexto não inicializado"));
    }

    // ===================================================================
    // ATALHOS - LEI DE DEMÉTER
    // ===================================================================

    public static String getCorrelationId() {
        return get().getCorrelationId();
    }

    public static String getOperationType() {
        return get().getOperationType();
    }

    public static Set<String> getActiveTags() {
        return get().getActiveTags();
    }
}
