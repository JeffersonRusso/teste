package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JsonNodeAccessor: Permite que o SpEL leia propriedades diretamente do JsonNode.
 */
@Component
public class JsonNodeAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{JsonNode.class, Map.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof JsonNode || target instanceof Map;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof Map<?, ?> map) {
            Object value = map.get(name);
            if (value instanceof JsonNode node) return unwrap(node);
            return new TypedValue(value);
        }

        if (target instanceof JsonNode node) {
            return unwrap(node.get(name));
        }

        return TypedValue.NULL;
    }

    private TypedValue unwrap(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return TypedValue.NULL;
        }
        
        if (node.isTextual()) return new TypedValue(node.asText());
        if (node.isNumber()) return new TypedValue(node.numberValue());
        if (node.isBoolean()) return new TypedValue(node.asBoolean());
        
        // Se for objeto/array, retorna o próprio JsonNode para continuar navegando
        return new TypedValue(node);
    }

    @Override public boolean canWrite(EvaluationContext context, Object target, String name) { return false; }
    @Override public void write(EvaluationContext context, Object target, String name, Object newValue) {}
}
