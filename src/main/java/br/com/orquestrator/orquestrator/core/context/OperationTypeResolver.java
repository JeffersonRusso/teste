package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ApiConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Component
public class OperationTypeResolver {

    public String resolve(Map<String, String> headers, Map<String, Object> body) {
        return extractFromBody(body)
                .or(() -> extractFromHeaders(headers))
                .orElse(ApiConstants.DEFAULT_OPERATION);
    }

    private Optional<String> extractFromBody(Map<String, Object> body) {
        return Optional.ofNullable(body)
                .map(map -> map.get(ApiConstants.BODY_OPERATION_TYPE))
                .map(Object::toString)
                .filter(StringUtils::hasText);
    }

    private Optional<String> extractFromHeaders(Map<String, String> headers) {
        return Optional.ofNullable(headers)
                .map(map -> map.get(ApiConstants.HEADER_OPERATION_TYPE))
                .filter(StringUtils::hasText);
    }
}
