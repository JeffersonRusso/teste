package br.com.orquestrator.orquestrator.infra.data.jackson;

import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JacksonDataFactory: Implementação da fábrica agnóstica usando Jackson.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JacksonDataFactory implements DataFactory {

    private final ObjectMapper objectMapper;

    @Override
    public DataNode createObject(Map<String, Object> map) {
        return new JacksonDataNode(objectMapper.valueToTree(map), objectMapper);
    }

    @Override
    public DataNode createValue(Object value) {
        if (value instanceof JsonNode jn) {
            return new JacksonDataNode(jn, objectMapper);
        }
        return new JacksonDataNode(objectMapper.valueToTree(value), objectMapper);
    }

    @Override
    public DataNode missing() {
        return new JacksonDataNode(MissingNode.getInstance(), objectMapper);
    }

    @Override
    public DataNode parse(String rawData) {
        try {
            return new JacksonDataNode(objectMapper.readTree(rawData), objectMapper);
        } catch (Exception e) {
            return missing();
        }
    }
}
