package br.com.orquestrator.orquestrator.infra.el;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpelExpressionEngine: Implementação do motor de expressões usando Spring SpEL.
 * Otimizado com cache de expressões compiladas.
 */
@Slf4j
@Component
public class SpelExpressionEngine implements ExpressionEngine {

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ParserContext templateContext = new TemplateParserContext("${", "}");
    
    // Cache de expressões para evitar parse repetitivo (Hot Path)
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>(1024);
    private final Map<String, Expression> templateCache = new ConcurrentHashMap<>(1024);

    @Override
    public <T> T evaluate(String expression, Object nativeContext, Class<T> targetType) {
        if (expression == null || expression.isBlank()) return null;
        
        try {
            Expression exp = expressionCache.computeIfAbsent(expression, parser::parseExpression);
            return exp.getValue((org.springframework.expression.EvaluationContext) nativeContext, targetType);
        } catch (Exception e) {
            log.warn("Falha ao avaliar expressão SpEL: {} | Erro: {}", expression, e.getMessage());
            return null;
        }
    }

    @Override
    public <T> T resolve(String template, Object nativeContext, Class<T> targetType) {
        if (template == null || template.isBlank()) return null;

        try {
            Expression exp = templateCache.computeIfAbsent(template, t -> parser.parseExpression(t, templateContext));
            return exp.getValue((org.springframework.expression.EvaluationContext) nativeContext, targetType);
        } catch (Exception e) {
            log.warn("Falha ao resolver template SpEL: {} | Erro: {}", template, e.getMessage());
            return (targetType == String.class) ? targetType.cast(template) : null;
        }
    }
}
