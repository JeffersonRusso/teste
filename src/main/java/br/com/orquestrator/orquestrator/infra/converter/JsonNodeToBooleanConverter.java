package br.com.orquestrator.orquestrator.infra.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JsonNodeToBooleanConverter implements Converter<JsonNode, Boolean> {

    @Override
    public Boolean convert(JsonNode source) {
        if (source.isMissingNode() || source.isNull()) {
            return null;
        }
        if (source.isBoolean()) {
            return source.asBoolean();
        }
        if (source.isTextual()) {
            return Boolean.parseBoolean(source.asText());
        }
        return null;
    }
}
