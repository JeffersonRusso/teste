package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JsonNodeAccessor: O tradutor universal entre Jackson e SpEL.
 * Garante que o SpEL trabalhe com tipos primitivos Java, evitando aspas extras e erros de conversão.
 */
@Component
public class JsonNodeAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        // Suporta tanto a navegação em nós quanto no mapa de inputs
        return new Class<?>[]{JsonNode.class, Map.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof JsonNode || target instanceof Map;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof Map<?, ?> map) {
            return unwrap(map.get(name));
        }

        if (target instanceof JsonNode node) {
            return unwrap(node.get(name));
        }

        return TypedValue.NULL;
    }

    /**
     * Desempacota um JsonNode para um tipo Java nativo que o SpEL entende.
     */
    private TypedValue unwrap(Object value) {
        if (!(value instanceof JsonNode node) || node.isMissingNode() || node.isNull()) {
            return TypedValue.NULL;
        }

        if (node.isTextual()) return new TypedValue(node.asText());
        if (node.isNumber()) return new TypedValue(node.numberValue());
        if (node.isBoolean()) return new TypedValue(node.asBoolean());

        // Se for objeto ou array, retorna o próprio JsonNode para continuar a navegação
        return new TypedValue(node);
    }

    @Override public boolean canWrite(EvaluationContext context, Object target, String name) { return false; }
    @Override public void write(EvaluationContext context, Object target, String name, Object newValue) {}
}
