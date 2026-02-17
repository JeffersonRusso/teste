package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Fábrica de contexto SpEL otimizada para Maps.
 */
@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor;
    private final DefaultConversionService spelConversionService;

    public StandardEvaluationContext create(Object root, Map<String, Object> variables) {
        var context = new StandardEvaluationContext(root);
        context.setPropertyAccessors(List.of(mapAccessor));
        context.setTypeConverter(new StandardTypeConverter(spelConversionService));
        
        if (root instanceof Map<?, ?> map) {
            map.forEach((k, v) -> context.setVariable(k.toString(), v));
        }
        if (variables != null) variables.forEach(context::setVariable);
        
        return context;
    }
}
