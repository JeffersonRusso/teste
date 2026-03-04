package br.com.orquestrator.orquestrator.core.context.identity;

import br.com.orquestrator.orquestrator.domain.ApiConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Component
public class HeaderOperationTypeProvider implements OperationTypeProvider {
    @Override
    public Optional<String> provide(Map<String, String> headers, Map<String, Object> body) {
        return Optional.ofNullable(headers)
                .map(map -> map.get(ApiConstants.HEADER_OPERATION_TYPE))
                .filter(StringUtils::hasText);
    }

    @Override public int getPriority() { return 20; }
}
