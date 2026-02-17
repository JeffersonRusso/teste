package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import java.util.Map;

/**
 * Ponto de entrada para criação de contextos de avaliação.
 */
public interface ExpressionService {

    EvaluationContext create(Object root);

    EvaluationContext create(Object root, Map<String, Object> variables);

    default EvaluationContext create(ExecutionContext context) {
        // Usa o ObjectNode root do contexto diretamente para evitar conversões
        return create(context.getRoot());
    }

    default EvaluationContext create(ExecutionContext context, Map<String, Object> variables) {
        return create(context.getRoot(), variables);
    }
}
