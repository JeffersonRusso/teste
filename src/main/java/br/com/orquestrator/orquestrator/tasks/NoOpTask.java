package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

public class NoOpTask implements Task {
    @Override
    public TaskResult execute(TaskContext context) {
        return TaskResult.success(DataValue.of(Map.of("status", "ok")), Map.of());
    }
}

@Component
class NoOpTaskProvider implements TaskProvider {
    @Override public String getType() { return "NO_OP"; }
    @Override public Optional<Class<?>> getConfigClass() { return Optional.empty(); }
    @Override public Task create(br.com.orquestrator.orquestrator.domain.model.TaskDefinition definition) {
        return new NoOpTask();
    }
}
