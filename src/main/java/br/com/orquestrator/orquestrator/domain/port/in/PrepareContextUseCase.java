package br.com.orquestrator.orquestrator.domain.port.in;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import java.util.Map;

/**
 * Port In: Define a intenção de preparar o contexto para uma operação.
 * Expurgado JsonNode em favor de Map para pureza do domínio.
 */
public interface PrepareContextUseCase {
    ExecutionContext execute(String operationType, Map<String, String> headers, Map<String, Object> rawBody);
}
