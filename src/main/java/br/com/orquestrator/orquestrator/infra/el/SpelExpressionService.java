package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.infra.json.JsonNodeAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SpelExpressionService implements ExpressionService, ExpressionEngine {

    private final SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.MIXED, this.getClass().getClassLoader());
    private final ExpressionParser parser = new SpelExpressionParser(config);
    private final TemplateParserContext templateContext = new TemplateParserContext();
    
    private static final MapAccessor MAP_ACCESSOR = new MapAccessor();
    private static final JsonNodeAccessor JSON_ACCESSOR = new JsonNodeAccessor();

    private final Map<String, Expression> cache = new ConcurrentHashMap<>();

    @Override
    public EvaluationContext create(Object root) {
        return new EvaluationContext(createNativeContext(root, Map.of()), this);
    }

    @Override
    public EvaluationContext create(Object root, Map<String, Object> variables) {
        return new EvaluationContext(createNativeContext(root, variables), this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T evaluate(String expression, Object nativeContext, Class<T> targetType) {
        if (!(nativeContext instanceof StandardEvaluationContext ctx)) {
            throw new IllegalArgumentException("Contexto nativo inválido para SpEL");
        }

        try {
            Expression exp = cache.computeIfAbsent(expression, k -> 
                k.contains("#{") ? parser.parseExpression(k, templateContext) : parser.parseExpression(k)
            );
            return exp.getValue(ctx, targetType);
        } catch (SpelEvaluationException e) {
            throw new IllegalArgumentException(String.format("Erro ao avaliar '%s': %s", expression, e.getMessage()), e);
        }
    }

    private StandardEvaluationContext createNativeContext(Object rootObject, Map<String, Object> variables) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setPropertyAccessors(List.of(MAP_ACCESSOR, JSON_ACCESSOR));
        
        // Se o rootObject for um TaskData, usamos a visão de Map para o SpEL
        Object effectiveRoot = rootObject;
        if (rootObject instanceof br.com.orquestrator.orquestrator.tasks.base.TaskData data) {
            effectiveRoot = data.asMap();
        }

        context.setRootObject(effectiveRoot);
        
        if (effectiveRoot instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedMap = (Map<String, Object>) map;
            context.setVariables(typedMap);
        }
        
        if (variables != null && !variables.isEmpty()) {
            variables.forEach(context::setVariable);
        }

        return context;
    }
}
