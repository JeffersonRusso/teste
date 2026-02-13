package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Especialista em resolver estruturas de configuração dinâmicas (JSON).
 * Aplica o EvaluationContext recursivamente em árvores de dados.
 */
@Component
@RequiredArgsConstructor
public class ConfigResolver {

    private final ObjectMapper objectMapper;

    public JsonNode resolve(JsonNode node, EvaluationContext context) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return node;
        }

        return switch (node.getNodeType()) {
            case OBJECT -> resolveObject((ObjectNode) node, context);
            case ARRAY  -> resolveArray((ArrayNode) node, context);
            case STRING -> resolveString(node.asText(), context);
            default     -> node;
        };
    }

    private JsonNode resolveObject(ObjectNode source, EvaluationContext context) {
        ObjectNode newNode = objectMapper.createObjectNode();
        source.fields().forEachRemaining(entry -> 
            newNode.set(entry.getKey(), resolve(entry.getValue(), context))
        );
        return newNode;
    }

    private JsonNode resolveArray(ArrayNode source, EvaluationContext context) {
        ArrayNode newNode = objectMapper.createArrayNode();
        source.forEach(item -> newNode.add(resolve(item, context)));
        return newNode;
    }

    private JsonNode resolveString(String text, EvaluationContext context) {
        Object resolved = context.resolve(text, Object.class);
        return objectMapper.valueToTree(resolved);
    }
}
