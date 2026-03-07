package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{ReadableContext.class, DataValue.class, JsonNode.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof ReadableContext || target instanceof DataValue || target instanceof JsonNode;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof ReadableContext rc) {
            return unwrapIfScalar(rc.get(name));
        }

        if (target instanceof JsonNode node) {
            return readFromJsonNode(node, name);
        }

        if (target instanceof DataValue dv) {
            return switch (dv) {
                case DataValue.Mapping(var fields, var type) -> unwrapIfScalar(DataValue.of(fields.get(name)));
                case DataValue.DomainObject(var obj, var type) -> {
                    if (obj instanceof JsonNode node) yield readFromJsonNode(node, name);
                    yield readReflectively(obj, name);
                }
                default -> TypedValue.NULL;
            };
        }

        return TypedValue.NULL;
    }

    private TypedValue readFromJsonNode(JsonNode node, String name) {
        JsonNode child = node.get(name);
        if (child == null || child.isMissingNode() || child.isNull()) {
            return TypedValue.NULL;
        }
        
        // Converte tipos básicos do Jackson para tipos Java nativos para o SpEL
        if (child.isTextual()) return new TypedValue(child.asText());
        if (child.isNumber()) return new TypedValue(child.numberValue());
        if (child.isBoolean()) return new TypedValue(child.asBoolean());
        
        return new TypedValue(child);
    }

    private TypedValue unwrapIfScalar(DataValue dv) {
        if (dv == null || dv instanceof DataValue.Empty) return TypedValue.NULL;
        
        return switch (dv) {
            case DataValue.Numeric(var n, var type) -> new TypedValue(n);
            case DataValue.Text(var s, var type) -> new TypedValue(s);
            case DataValue.Logic(var b, var type) -> new TypedValue(b);
            case DataValue.DomainObject(var obj, var type) -> new TypedValue(obj);
            default -> new TypedValue(dv);
        };
    }

    private TypedValue readReflectively(Object obj, String name) {
        return TypedValue.NULL;
    }

    @Override public boolean canWrite(EvaluationContext context, Object target, String name) { return false; }
    @Override public void write(EvaluationContext context, Object target, String name, Object newValue) {}
}
