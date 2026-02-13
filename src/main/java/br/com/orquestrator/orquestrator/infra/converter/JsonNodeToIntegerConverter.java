package br.com.orquestrator.orquestrator.infra.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JsonNodeToIntegerConverter implements Converter<JsonNode, Integer> {

    @Override
    public Integer convert(JsonNode source) {
        if (source.isMissingNode() || source.isNull()) {
            return null;
        }
        if (source.isNumber()) {
            return source.asInt();
        }
        if (source.isTextual()) {
            try {
                return Integer.parseInt(source.asText());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
