package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.context.ContextSchema;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor = new MapAccessor();
    private final ExecutionContextAccessor contextAccessor;
    private final DefaultConversionService spelConversionService;
    
    // Reutiliza o conversor de tipos para evitar alocações
    private final TypeConverter sharedTypeConverter = new StandardTypeConverter(new DefaultConversionService());

    public StandardEvaluationContext create(Object root) {
        // Cria um contexto leve
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        
        // Adiciona os acessores pré-instanciados (Singleton)
        context.addPropertyAccessor(contextAccessor);
        context.addPropertyAccessor(mapAccessor);
        
        context.setTypeConverter(sharedTypeConverter);

        if (root instanceof ReadableContext rc) {
            injectSovereignVariables(context, rc);
        }
        
        return context;
    }

    private void injectSovereignVariables(StandardEvaluationContext context, ReadableContext rc) {
        // Usa os namespaces soberanos definidos no schema
        for (String ns : ContextSchema.sovereignNamespaces()) {
            context.setVariable(ns, rc.get(ns));
        }
    }
}
