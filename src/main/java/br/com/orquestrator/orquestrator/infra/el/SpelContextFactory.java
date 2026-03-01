package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fábrica de contexto SpEL otimizada.
 */
@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor;
    private final ExecutionContextAccessor executionContextAccessor;
    private final DefaultConversionService spelConversionService;
    private final ExpressionEngine expressionEngine;
    
    private List<PropertyAccessor> cachedAccessors;
    private TypeConverter cachedConverter;

    public EvaluationContext create(ExecutionContext context) {
        return create((Object) context, null);
    }

    public EvaluationContext create(Object root) {
        return create(root, null);
    }

    public EvaluationContext create(Object root, Map<String, Object> variables) {
        var nativeContext = new StandardEvaluationContext(root);
        
        nativeContext.setPropertyAccessors(getAccessors());
        nativeContext.setTypeConverter(getConverter());
        
        if (variables != null && !variables.isEmpty()) {
            variables.forEach(nativeContext::setVariable);
        }
        
        return new EvaluationContext(nativeContext, expressionEngine);
    }

    private List<PropertyAccessor> getAccessors() {
        if (cachedAccessors == null) {
            var accessors = new ArrayList<PropertyAccessor>();
            accessors.add(executionContextAccessor);
            accessors.add(mapAccessor);
            cachedAccessors = List.copyOf(accessors);
        }
        return cachedAccessors;
    }

    private TypeConverter getConverter() {
        if (cachedConverter == null) {
            cachedConverter = new StandardTypeConverter(spelConversionService);
        }
        return cachedConverter;
    }
}
