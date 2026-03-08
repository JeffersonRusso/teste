package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SpelContextFactory: Cria o contexto de avaliação SpEL para o Shadow Context.
 * Otimizado para Zero-Copy usando DataValueAccessor.
 */
@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor = new MapAccessor();
    private final DataValueAccessor dataValueAccessor;

    public StandardEvaluationContext create(Object root) {
        // Agora passamos o root (Map<String, DataValue>) diretamente!
        // Não há mais conversão para Map<String, Object>.
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        
        // Adicionamos o nosso acessor customizado para DataValue
        context.addPropertyAccessor(dataValueAccessor);
        context.addPropertyAccessor(mapAccessor);
        
        return context;
    }
}
