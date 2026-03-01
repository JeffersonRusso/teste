package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpelExpressionService implements ExpressionService {

    private final SpelContextFactory contextFactory;

    @Override
    public <T> T evaluate(String expression, Class<T> type) {
        return getSovereignContext().evaluate(expression, type);
    }

    @Override
    public <T> T evaluate(Object root, String expression, Class<T> type) {
        // Cria um contexto temporário para o objeto raiz fornecido
        return contextFactory.create(root).evaluate(expression, type);
    }

    @Override
    public <T> T resolve(String template, Class<T> type) {
        return getSovereignContext().resolve(template, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> resolveMap(Map<String, Object> source) {
        if (source == null) return Map.of();
        Map<String, Object> resolved = new HashMap<>();
        var eval = getSovereignContext();

        source.forEach((key, value) -> {
            if (value instanceof String str && (str.contains("#") || str.contains("${"))) {
                resolved.put(key, eval.resolve(str, Object.class));
            } else if (value instanceof Map) {
                resolved.put(key, resolveMap((Map<String, Object>) value));
            } else {
                resolved.put(key, value);
            }
        });

        return resolved;
    }

    private EvaluationContext getSovereignContext() {
        if (!ContextHolder.EVAL_CONTEXT.isBound()) {
            // Fallback: Se não houver contexto no escopo, cria um a partir do contexto global
            return contextFactory.create(ContextHolder.CONTEXT.get());
        }
        return ContextHolder.EVAL_CONTEXT.get();
    }
}
