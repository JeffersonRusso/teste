package br.com.orquestrator.orquestrator.domain;

/**
 * Constantes para chaves reservadas do ExecutionContext.
 */
public final class ContextKey {

    private ContextKey() {}

    public static final String RAW = "raw";
    public static final String STANDARD = "standard";
    public static final String HEADER = "header";
    public static final String TAGS = "tags";
    public static final String OPERATION_TYPE = "operation_type";
    public static final String PARAMS = "params";
}
