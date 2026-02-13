package br.com.orquestrator.orquestrator.domain;

/**
 * Constantes para chaves reservadas do ExecutionContext.
 * Evita typos e facilita a manutenção do que é "sistema" vs "dinâmico".
 */
public final class ContextKey {

    private ContextKey() {
        // Utility class
    }

    public static final String RAW = "raw";
    public static final String STANDARD = "standard";
    public static final String HEADER = "header";
    public static final String TAGS = "tags";
    public static final String OPERATION_TYPE = "operation_type";
    public static final String PRODUCT_PARAMS = "product_params";
    public static final String PARAMS = "params";
    public static final String CONTEXT = "context";
    public static final String JSON_MAPPER = "jsonMapper";
    public static final String VALUE = "value";
    
    // Prefixo para armazenar spans por task (evita colisão em paralelo)
    public static final String SPAN_PREFIX = "sys.span.";
}
