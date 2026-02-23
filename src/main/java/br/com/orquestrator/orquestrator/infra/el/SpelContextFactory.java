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

import java.util.List;
import java.util.Map;

/**
 * Fábrica de contexto SpEL otimizada.
 * Otimizado para evitar alocações repetitivas de Accessors e Converters.
 */
@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor;
    private final DefaultConversionService spelConversionService;
    
    // OTIMIZAÇÃO: Cache de componentes imutáveis do contexto
    private List<PropertyAccessor> cachedAccessors;
    private TypeConverter cachedConverter;

    public StandardEvaluationContext create(Object root, Map<String, Object> variables) {
        var context = new StandardEvaluationContext(root);
        
        // OTIMIZAÇÃO: Evita criar List.of e StandardTypeConverter em cada chamada
        context.setPropertyAccessors(getAccessors());
        context.setTypeConverter(getConverter());
        
        if (root instanceof ExecutionContext ctx) {
            context.setRootObject(ctx.getRoot());
        }

        if (variables != null && !variables.isEmpty()) {
            variables.forEach(context::setVariable);
        }
        
        return context;
    }

    private List<PropertyAccessor> getAccessors() {
        if (cachedAccessors == null) {
            cachedAccessors = List.of(mapAccessor);
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
