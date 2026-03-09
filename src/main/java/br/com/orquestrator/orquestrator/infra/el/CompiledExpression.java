package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * CompiledExpression: O contrato para uma bolinha de lógica.
 * Agora retorna JsonNode.
 */
public interface CompiledExpression {
    
    JsonNode evaluate(Object root);

    default <T> T evaluate(Object root, Class<T> targetType) {
        // Implementação padrão pode ser sobrescrita
        return null; 
    }

    CompiledExpression IDENTITY = (root) -> JsonNodeFactory.instance.pojoNode(root);
}
