package br.com.orquestrator.orquestrator.infra.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Conversor técnico: Transforma Map em String (JSON) de forma transparente para o SpEL.
 */
@Component
@RequiredArgsConstructor
public class MapToJsonStringConverter implements Converter<Map<?, ?>, String> {
    private final ObjectMapper mapper;

    @Override
    public String convert(Map<?, ?> source) {
        if (source == null) return null;
        try {
            return mapper.writeValueAsString(source);
        } catch (Exception e) {
            return source.toString();
        }
    }
}
