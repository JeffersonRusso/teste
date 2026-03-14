package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.ports.output.DataConverter;
import br.com.orquestrator.orquestrator.infra.el.CompiledExpression;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * CompiledConfiguration: Configuração capaz de resolver expressões dinâmicas.
 * 100% Pura: Sem dependência de ObjectMapper.
 */
@RequiredArgsConstructor
public class CompiledConfiguration<T> {

    private final Map<String, Object> rawConfig;
    private final Class<T> configClass;
    private final Map<String, CompiledExpression> expressions;
    private final DataConverter dataConverter;

    public T resolve(Map<String, ?> inputs) {
        if (expressions.isEmpty()) {
            return dataConverter.convert(rawConfig, configClass);
        }

        Map<String, Object> resolvedConfig = new HashMap<>(rawConfig);
        expressions.forEach((key, expr) -> {
            Object result = expr.evaluate(inputs);
            resolvedConfig.put(key, result);
        });

        return dataConverter.convert(resolvedConfig, configClass);
    }
}
