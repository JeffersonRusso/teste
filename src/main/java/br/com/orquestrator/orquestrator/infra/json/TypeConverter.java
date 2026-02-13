package br.com.orquestrator.orquestrator.infra.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TypeConverter {

    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToMap(Object value) {
        if (value == null) return null;
        if (value instanceof Map) return (Map<String, Object>) value;
        if (value instanceof JsonNode) return objectMapper.convertValue(value, Map.class);
        return objectMapper.convertValue(value, Map.class);
    }
}
