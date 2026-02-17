package br.com.orquestrator.orquestrator.core.engine.result;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class MetadataProcessor implements TaskResultProcessor {
    @Override
    public void process(TaskResult result, TaskDefinition definition, ExecutionContext context) {
        String nodeId = definition.getNodeId().value();
        context.setStatus(nodeId, result.status());
        if (result.metadata() != null) {
            result.metadata().forEach((k, v) -> context.track(nodeId, k, v));
        }
    }
}
