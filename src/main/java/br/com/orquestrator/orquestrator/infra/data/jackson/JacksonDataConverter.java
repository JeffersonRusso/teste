package br.com.orquestrator.orquestrator.infra.data.jackson;

import br.com.orquestrator.orquestrator.core.ports.output.DataConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JacksonDataConverter: Adapter que utiliza Jackson para realizar conversões tipadas.
 */
@Component
@RequiredArgsConstructor
public class JacksonDataConverter implements DataConverter {

    private final ObjectMapper objectMapper;

    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        if (source == null) return null;
        return objectMapper.convertValue(source, targetType);
    }
}
