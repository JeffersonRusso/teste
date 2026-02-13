package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import java.util.Map;

/**
 * Ponto de entrada para criação de contextos de avaliação.
 */
public interface ExpressionService {

    EvaluationContext create(Object root);

    EvaluationContext create(Object root, Map<String, Object> variables);

    default EvaluationContext create(ExecutionContext context) {
        return create(context.getDataStore());
    }

    default EvaluationContext create(TaskData data) {
        // Como TaskData é uma interface, o motor SpEL saberá lidar com ela
        // se o rootObject for a própria instância de TaskData.
        return create((Object) data);
    }
}
