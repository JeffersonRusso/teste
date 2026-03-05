package br.com.orquestrator.orquestrator.domain;

public final class ApiConstants {

    private ApiConstants() {}

    public static final String HEADER_OPERATION_TYPE = "X-Operation-Type";
    public static final String BODY_OPERATION_TYPE = "operationType";
    
    // NOVAS CHAVES DO CONTRATO
    public static final String BODY_ORDER_ID = "orderId";
    public static final String BODY_OPERATION_DATA = "operation";

    public static final String DEFAULT_OPERATION = "STANDARD_RISK";
}
