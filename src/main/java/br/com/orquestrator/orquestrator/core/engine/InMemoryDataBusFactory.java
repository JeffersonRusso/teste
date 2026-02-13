package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InMemoryDataBusFactory implements DataBusFactory {

    @Override
    public DataBus create(ExecutionContext context, List<TaskDefinition> tasks) {
        return new DataBus(context, tasks);
    }
}
