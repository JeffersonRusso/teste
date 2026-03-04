package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.context.identity.OperationTypeProvider;
import br.com.orquestrator.orquestrator.domain.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OperationTypeResolver: Coordenador da identificação da intenção do request.
 * Utiliza uma cadeia de provedores para resolver o tipo de operação.
 */
@Component
public class OperationTypeResolver {

    private final List<OperationTypeProvider> providers;

    public OperationTypeResolver(List<OperationTypeProvider> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt(OperationTypeProvider::getPriority))
                .toList();
    }

    public String resolve(Map<String, String> headers, Map<String, Object> body) {
        for (var provider : providers) {
            Optional<String> type = provider.provide(headers, body);
            if (type.isPresent()) return type.get();
        }
        return ApiConstants.DEFAULT_OPERATION;
    }
}
