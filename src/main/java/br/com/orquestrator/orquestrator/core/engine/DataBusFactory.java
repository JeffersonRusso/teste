package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;

import java.util.List;

public interface DataBusFactory {
    DataBus create(ExecutionContext context, List<TaskDefinition> tasks);
}
