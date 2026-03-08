package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;

/**
 * CompiledExpression: O contrato para uma bolinha de lógica.
 */
public interface CompiledExpression {
    
    DataValue evaluate(Object root);

    default <T> T evaluate(Object root, Class<T> targetType) {
        Object val = evaluate(root).raw();
        return targetType.isInstance(val) ? targetType.cast(val) : null;
    }

    default String rootField() { return ""; }

    default void setValue(Object root, Object value) {}

    // Identidade: A bolinha que apenas passa o dado adiante.
    CompiledExpression IDENTITY = (root) -> DataValueFactory.of(root);
}
