package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;

/**
 * ConstantCompiledExpression: Bolinha de valor fixo.
 */
@RequiredArgsConstructor
public final class ConstantCompiledExpression implements CompiledExpression {
    
    private final Object value;

    @Override
    public DataValue evaluate(Object root) {
        return DataValue.of(value);
    }
}
