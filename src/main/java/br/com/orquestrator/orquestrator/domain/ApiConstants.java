package br.com.orquestrator.orquestrator.domain;

/**
 * Constantes globais da API do Orquestrador.
 * Define o contrato único e soberano para identificação de operações.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Utility class
    }

    /** Header padrão para identificar o tipo de operação */
    public static final String HEADER_OPERATION_TYPE = "X-Operation-Type";

    /** Campo padrão no corpo JSON para identificar o tipo de operação */
    public static final String BODY_OPERATION_TYPE = "operationType";

    /** Operação padrão caso nenhuma seja informada */
    public static final String DEFAULT_OPERATION = "STANDARD_RISK";
}
