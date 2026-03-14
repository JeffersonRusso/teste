package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpelExpressionEngine: Motor de expressões SpEL com suporte a Templates.
 * 
 * Capaz de processar strings mistas como "cache_#{id}".
 */
@Component
@RequiredArgsConstructor
public class SpelExpressionEngine implements ExpressionEngine {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final SpelContextFactory contextFactory;
    private final Map<String, CompiledExpression> cache = new ConcurrentHashMap<>();
    private final ParserContext templateContext = new TemplateParserContext("#{", "}");

    @Override
    public CompiledExpression compile(Object expression) {
        if (expression == null) return null;
        
        String expressionString = expression.toString();
        
        return cache.computeIfAbsent(expressionString, key -> {
            // Se contiver o padrão de template #{, usa o context de template
            if (key.contains("#{")) {
                return new SpelCompiledExpression(parser.parseExpression(key, templateContext), contextFactory);
            }
            // Caso contrário, trata como expressão pura (mais rápido)
            return new SpelCompiledExpression(parser.parseExpression(key), contextFactory);
        });
    }
}
