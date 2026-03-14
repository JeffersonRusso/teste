package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.ports.output.DataConverter;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TaskBindingResolver: Motor de compilação de configurações agnóstico.
 */
@Component
@RequiredArgsConstructor
public class TaskBindingResolver {

    private final ExpressionEngine expressionEngine;
    private final DataConverter dataConverter; // Injeção via Porta
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("#\\{([^}]+)\\}");

    public <T> CompiledConfiguration<T> compile(Map<String, Object> configMap, Class<T> configClass) {
        Map<String, Object> rawConfig = new HashMap<>();
        Map<String, br.com.orquestrator.orquestrator.infra.el.CompiledExpression> expressions = new HashMap<>();

        if (configMap != null) {
            configMap.forEach((key, value) -> {
                if (value instanceof String s) {
                    Matcher matcher = EXPRESSION_PATTERN.matcher(s);
                    if (matcher.find()) {
                        expressions.put(key, expressionEngine.compile(s));
                    }
                }
                rawConfig.put(key, value);
            });
        }

        return new CompiledConfiguration<>(rawConfig, configClass, expressions, dataConverter);
    }
}
