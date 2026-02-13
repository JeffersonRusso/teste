package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ApiConstants;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Component
public class OperationTypeResolver {

    public String resolve(Map<String, String> headers, JsonNode body) {
        return extractFromBody(body)
                .or(() -> extractFromHeaders(headers))
                .orElse(ApiConstants.DEFAULT_OPERATION);
    }

    private Optional<String> extractFromBody(JsonNode body) {
        return Optional.ofNullable(body)
                .map(json -> json.get(ApiConstants.BODY_OPERATION_TYPE))
                .map(JsonNode::asText)
                .filter(StringUtils::hasText);
    }

    private Optional<String> extractFromHeaders(Map<String, String> headers) {
        return Optional.ofNullable(headers)
                .map(map -> map.get(ApiConstants.HEADER_OPERATION_TYPE))
                .filter(StringUtils::hasText);
    }
}
