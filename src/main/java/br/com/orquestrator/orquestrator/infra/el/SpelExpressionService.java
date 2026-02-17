package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Motor de Expressão: Focado em performance e separação de intenção.
 */
@Service
@RequiredArgsConstructor
public class SpelExpressionService implements ExpressionService, ExpressionEngine {

    private final ExpressionParser orchestratorExpressionParser;
    private final TemplateParserContext templateParserContext;
    private final SpelContextFactory contextFactory;
    
    private final Map<String, Expression> logicCache = new ConcurrentHashMap<>();
    private final Map<String, Expression> templateCache = new ConcurrentHashMap<>();

    @Override
    public EvaluationContext create(Object root) {
        return create(root, Map.of());
    }

    @Override
    public EvaluationContext create(Object root, Map<String, Object> variables) {
        var nativeContext = contextFactory.create(root, variables);
        return new EvaluationContext(nativeContext, this);
    }

    @Override
    public <T> T evaluate(String expression, Object nativeContext, Class<T> targetType) {
        try {
            Expression exp = logicCache.computeIfAbsent(expression, orchestratorExpressionParser::parseExpression);
            return exp.getValue((StandardEvaluationContext) nativeContext, targetType);
        } catch (SpelEvaluationException e) {
            throw new IllegalArgumentException(STR."Erro na lógica SpEL '\{expression}': \{e.getMessage()}", e);
        }
    }

    @Override
    public <T> T resolve(String template, Object nativeContext, Class<T> targetType) {
        try {
            Expression exp = templateCache.computeIfAbsent(template, k -> orchestratorExpressionParser.parseExpression(k, templateParserContext));
            return exp.getValue((StandardEvaluationContext) nativeContext, targetType);
        } catch (SpelEvaluationException e) {
            throw new IllegalArgumentException(STR."Erro no template SpEL '\{template}': \{e.getMessage()}", e);
        }
    }
}
