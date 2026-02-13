package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * Base para implementações de tasks que seguem o contrato de dados restrito.
 */
@Slf4j
public abstract class AbstractTask implements Task, ConfigurableTask {

    protected final TaskDefinition definition;

    protected AbstractTask(final TaskDefinition definition) {
        this.definition = definition;
    }

    @Override
    public abstract void execute(TaskData data);

    @Override
    public abstract void validateConfig();
}
