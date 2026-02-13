package br.com.orquestrator.orquestrator.infra.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JsonNodeToStringConverter implements Converter<JsonNode, String> {

    @Override
    public String convert(JsonNode source) {
        if (source.isMissingNode() || source.isNull()) {
            return null;
        }
        if (source.isTextual()) {
            return source.asText();
        }
        return source.toString();
    }
}
