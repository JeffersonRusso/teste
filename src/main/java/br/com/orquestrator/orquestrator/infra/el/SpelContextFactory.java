package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SpelContextFactory: Fábrica de contextos SpEL.
 * Mantida apenas para encapsular a configuração de conversores e acessores customizados.
 */
@Component
public class SpelContextFactory {

    private final DataNodePropertyAccessor dataNodeAccessor = new DataNodePropertyAccessor();
    private final DataNodeAwareMapAccessor mapAccessor = new DataNodeAwareMapAccessor();
    private final StandardTypeConverter typeConverter;

    public SpelContextFactory() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(DataNode.class, Object.class, DataNode::asNative);
        conversionService.addConverter(DataNode.class, String.class, dn -> String.valueOf(dn.asNative()));
        this.typeConverter = new StandardTypeConverter(conversionService);
    }

    public StandardEvaluationContext create(Object root) {
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        context.addPropertyAccessor(mapAccessor);
        context.addPropertyAccessor(dataNodeAccessor);
        context.setTypeConverter(typeConverter);
        return context;
    }
}
