package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.s3.S3Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3TaskProvider implements TaskProvider {

    private final ObjectMapper objectMapper;
    private final ExpressionService expressionService;

    @Override
    public String getType() {
        return "S3_EXPORT";
    }

    @Override
    public Task create(TaskDefinition definition) {
        return new S3Task(definition, objectMapper, expressionService);
    }
}
