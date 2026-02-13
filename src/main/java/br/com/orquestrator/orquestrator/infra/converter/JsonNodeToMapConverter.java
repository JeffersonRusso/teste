package br.com.orquestrator.orquestrator.infra.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonNodeToMapConverter implements Converter<JsonNode, Map<String, Object>> {

    private final ObjectMapper objectMapper;
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    @Override
    public Map<String, Object> convert(JsonNode source) {
        if (source.isMissingNode()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.convertValue(source, MAP_TYPE);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
