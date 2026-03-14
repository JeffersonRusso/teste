package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * S3Executor: Responsável pela comunicação técnica com o Amazon S3.
 */
@Component
@RequiredArgsConstructor
public class S3Executor {

    private final DataFactory dataFactory;

    public TaskResult execute(S3TaskConfiguration config, JsonNode input) {
        // TODO: Implementar lógica real de S3 (AWS SDK)
        // Por enquanto, retornamos um sucesso vazio seguindo o novo padrão DataNode.
        return TaskResult.success(dataFactory.missing());
    }
}
