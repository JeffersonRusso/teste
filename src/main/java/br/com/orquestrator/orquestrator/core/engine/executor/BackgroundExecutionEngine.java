package br.com.orquestrator.orquestrator.core.engine.executor;

import br.com.orquestrator.orquestrator.core.context.OrquestratorContext;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * BackgroundExecutionEngine: Executor para tarefas agendadas ou assíncronas.
 * 
 * Responsável por criar um contexto de identidade para execuções que não
 * se originam de uma requisição web (ex: Cron Jobs).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackgroundExecutionEngine {

    private final TaskRegistry taskRegistry;
    private final IdGenerator idGenerator;

    public void execute(TaskDefinition def) {
        RequestIdentity identity = new RequestIdentity(
            idGenerator.generateFastId(),
            "BACKGROUND_" + def.nodeId().value(),
            "ORDER_BG",
            idGenerator.generateFastId(),
            Collections.emptySet()
        );

        Task executable = taskRegistry.getCompiledTask(def);

        try {
            OrquestratorContext.runWith(identity, () -> {
                TaskExecutionContext context = new TaskExecutionContext(def, Collections.emptyMap());
                executable.execute(context);
                return null;
            });
        } catch (Exception e) {
            log.error("Falha na task background [{}]: {}", def.nodeId().value(), e.getMessage());
        }
    }
}
