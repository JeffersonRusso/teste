package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Fábrica de contexto SpEL otimizada.
 */
@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor;
    private final DefaultConversionService spelConversionService;

    public StandardEvaluationContext create(Object root, Map<String, Object> variables) {
        // Otimização: Usamos o root diretamente. 
        // Se o root for um Map, o MapAccessor cuidará do acesso sem precisarmos copiar para variáveis.
        var context = new StandardEvaluationContext(root);
        context.setPropertyAccessors(List.of(mapAccessor));
        context.setTypeConverter(new StandardTypeConverter(spelConversionService));
        
        // Se o root for o nosso ExecutionContext, usamos o mapa interno como root real
        if (root instanceof ExecutionContext ctx) {
            context.setRootObject(ctx.getRoot());
        }

        if (variables != null && !variables.isEmpty()) {
            variables.forEach(context::setVariable);
        }
        
        return context;
    }
}
