package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * TaskResultMapper: Integra o resultado da tarefa ao contexto.
 * Simplicidade absoluta: Grava o corpo da resposta na chave configurada.
 */
@Component
@RequiredArgsConstructor
public class TaskResultMapper {

    private final ExpressionService expressionService;

    public void map(Pipeline.TaskNode node, TaskResult result, ExecutionContext context) {
        context.put(node.nodeId() + ".status", result.status());

        if (result.isSuccess() && result.body() != null) {
            Object rawBody = result.body().raw();
            context.put(node.nodeId(), rawBody);

            if (node.outputs() != null) {
                for (var output : node.outputs()) {
                    // Usa o novo método que avalia contra uma raiz específica
                    Object value = expressionService.evaluate(rawBody, ".", Object.class); // Exemplo simplificado
                    // Nota: Se o path for ".", o evaluate deve saber lidar ou usamos lógica simples aqui
                    context.put(output.targetKey(), rawBody);
                }
            }
        }
    }
}
