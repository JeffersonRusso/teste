package br.com.orquestrator.orquestrator.domain;

/**
 * Constantes globais da API do Orquestrador.
 * Define contratos de headers e campos de corpo padrão.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Utility class
    }

    public static final String HEADER_OPERATION_TYPE = "X-Operation-Type";
    public static final String BODY_OPERATION_TYPE = "operation_type";
    public static final String DEFAULT_OPERATION = "STANDARD_RISK";
}
