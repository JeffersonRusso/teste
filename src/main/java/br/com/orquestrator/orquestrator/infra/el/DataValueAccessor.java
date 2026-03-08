package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.domain.model.DataValueNavigator;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DataValueAccessor: O único acessor necessário para o motor SpEL.
 * Agora usa DataValueNavigator para consistência de navegação.
 */
@Component
public class DataValueAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{DataValue.class, DataValue.Mapping.class, JsonNode.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof DataValue || target instanceof JsonNode;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof JsonNode node) {
            return readFromJsonNode(node, name);
        }

        if (target instanceof DataValue dv) {
            DataValue result = DataValueNavigator.navigate(dv, name);
            return unwrap(result);
        }

        return TypedValue.NULL;
    }

    private TypedValue readFromJsonNode(JsonNode node, String name) {
        JsonNode child = node.get(name);
        if (child == null || child.isMissingNode() || child.isNull()) {
            return TypedValue.NULL;
        }
        
        if (child.isTextual()) return new TypedValue(child.asText());
        if (child.isNumber()) return new TypedValue(child.numberValue());
        if (child.isBoolean()) return new TypedValue(child.asBoolean());
        
        return new TypedValue(child);
    }

    private TypedValue unwrap(DataValue dv) {
        if (dv == null || dv.isEmpty()) return TypedValue.NULL;
        
        return switch (dv) {
            case DataValue.Numeric(var n, var type) -> new TypedValue(n);
            case DataValue.Text(var s, var type) -> new TypedValue(s);
            case DataValue.Logic(var b, var type) -> new TypedValue(b);
            case DataValue.DomainObject(var obj, var type) -> new TypedValue(obj);
            default -> new TypedValue(dv);
        };
    }

    @Override public boolean canWrite(EvaluationContext context, Object target, String name) { return false; }
    @Override public void write(EvaluationContext context, Object target, String name, Object newValue) {}
}
