package br.com.orquestrator.orquestrator.core.context.identity;

import java.util.Map;
import java.util.Optional;

/**
 * OperationTypeProvider: Contrato para diferentes formas de identificar a operação.
 */
public interface OperationTypeProvider {
    Optional<String> provide(Map<String, String> headers, Map<String, Object> body);
    int getPriority(); // Define a ordem de tentativa
}
