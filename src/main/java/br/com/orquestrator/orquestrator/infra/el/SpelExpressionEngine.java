package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpelExpressionEngine implements ExpressionEngine {

    private final SpelExpressionParser parser;
    private final SpelContextFactory contextFactory;
    private final ParserContext context = new TemplateParserContext("#{", "}");
    private final Map<String, Expression> cache = new ConcurrentHashMap<>(1024);

    @Override
    public DataValue evaluate(Object value, Object root) {
        if (value == null) return new DataValue.Empty();
        if (!(value instanceof String expr)) return DataValue.of(value);
        if (expr.isBlank()) return DataValue.of(root);

        return execute(parse(expr), root);
    }

    @Override
    public <T> T evaluate(Object value, Object root, Class<T> targetType) {
        if (value == null) return null;
        if (!(value instanceof String expr)) {
            return targetType.isInstance(value) ? targetType.cast(value) : null;
        }

        try {
            return parse(expr).getValue(contextFactory.create(root), targetType);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Expression parse(String expression) {
        return cache.computeIfAbsent(expression, e -> parser.parseExpression(e, context));
    }

    @Override
    public DataValue execute(Expression expression, Object root) {
        try {
            Object result = expression.getValue(contextFactory.create(root));
            return DataValue.of(result);
        } catch (Exception e) {
            // Se falhar a avaliação (ex: literal sem delimitadores), retorna a string original
            return DataValue.of(expression.getExpressionString());
        }
    }

    @Override
    public void setValue(Object root, String path, Object value) {
        try {
            parser.parseExpression(path).setValue(contextFactory.create(root), value);
        } catch (Exception e) {
            log.error("Falha ao gravar no path: {} | Erro: {}", path, e.getMessage());
        }
    }
}
