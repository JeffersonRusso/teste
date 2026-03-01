package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TypedTaskProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Provedor de SpelTask: Simples e padronizado.
 */
@Component
public class SpelTaskProvider extends TypedTaskProvider<SpelTaskConfiguration> {

    private final ExpressionService expressionService;

    public SpelTaskProvider(ObjectMapper objectMapper, ExpressionService expressionService) {
        super(objectMapper, SpelTaskConfiguration.class, "SPEL");
        this.expressionService = expressionService;
    }

    @Override
    protected Task createInternal(TaskDefinition def, SpelTaskConfiguration config) {
        return new SpelTask(def, expressionService, config);
    }
}
