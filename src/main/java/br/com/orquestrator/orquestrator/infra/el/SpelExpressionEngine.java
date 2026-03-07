package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SpelExpressionEngine implements ExpressionEngine {

    private final SpelExpressionParser parser;
    private final SpelContextFactory contextFactory;
    private final ParserContext templateContext = new TemplateParserContext("#{", "}");
    private final Map<Object, CompiledExpression> cache = new ConcurrentHashMap<>(1024);

    @Override
    public CompiledExpression compile(Object value) {
        if (value == null) return CompiledExpression.IDENTITY;
        
        return cache.computeIfAbsent(value, v -> {
            if (v instanceof String e) {
                if (e.isBlank() || ".".equals(e.trim())) return CompiledExpression.IDENTITY;
                return new SpelCompiledExpression(parser.parseExpression(e, templateContext), contextFactory);
            }
            return new ConstantCompiledExpression(v);
        });
    }
}
