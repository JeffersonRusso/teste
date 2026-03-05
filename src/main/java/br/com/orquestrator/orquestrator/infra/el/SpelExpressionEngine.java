package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpelExpressionEngine implements ExpressionEngine {

    private final SpelExpressionParser parser;
    private final SpelContextFactory contextFactory;
    private final ParserContext templateContext = new TemplateParserContext("${", "}");
    private final Map<String, Expression> cache = new ConcurrentHashMap<>(1024);

    @Override
    public Expression parse(String expression) {
        return cache.computeIfAbsent(expression, parser::parseExpression);
    }

    @Override
    public <T> T execute(Expression compiledExpression, Object root, Class<T> targetType) {
        try {
            return compiledExpression.getValue(contextFactory.create(root), targetType);
        } catch (Exception e) {
            log.warn("Falha ao executar expressão compilada: {} | Erro: {}", compiledExpression.getExpressionString(), e.getMessage());
            return null;
        }
    }

    @Override
    public <T> T evaluate(String expression, Object root, Class<T> targetType) {
        return execute(parse(expression), root, targetType);
    }

    @Override
    public <T> T resolve(String template, Object root, Class<T> targetType) {
        try {
            Expression exp = cache.computeIfAbsent("tpl:" + template, t -> parser.parseExpression(template, templateContext));
            return exp.getValue(contextFactory.create(root), targetType);
        } catch (Exception e) {
            log.warn("Falha ao resolver template: {} | Erro: {}", template, e.getMessage());
            return (targetType == String.class) ? targetType.cast(template) : null;
        }
    }

    @Override
    public void setValue(Object root, String path, Object value) {
        try {
            parse(path).setValue(contextFactory.create(root), value);
        } catch (Exception e) {
            log.error("Falha ao gravar no path: {} | Erro: {}", path, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> resolveMap(Map<String, Object> source, Object root) {
        if (source == null || source.isEmpty()) return Map.of();
        Map<String, Object> resolved = new HashMap<>((int) (source.size() / 0.75f) + 1);
        source.forEach((key, value) -> {
            if (value instanceof String str && (str.contains("#") || str.contains("${"))) {
                resolved.put(key, resolve(str, root, Object.class));
            } else {
                resolved.put(key, value);
            }
        });
        return resolved;
    }
}
