package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConstantCompiledExpression implements CompiledExpression {
    private final Object value;

    @Override
    public JsonNode evaluate(Object root) {
        return JsonNodeFactory.instance.pojoNode(value);
    }
}
