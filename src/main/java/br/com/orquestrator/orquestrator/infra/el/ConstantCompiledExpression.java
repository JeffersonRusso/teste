package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.RequiredArgsConstructor;

/**
 * ConstantCompiledExpression: Representa um valor estático com suporte a tipos.
 */
@RequiredArgsConstructor
public final class ConstantCompiledExpression implements CompiledExpression {
    private final Object value;

    @Override
    public JsonNode evaluate(Object root) {
        return com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.pojoNode(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(Object root, Class<T> targetType) {
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
}
