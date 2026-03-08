package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * BackgroundExecutionEngine: Executa tarefas em segundo plano.
 * Agora desacoplado do ExecutionContext e focado no Shadow Context.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackgroundExecutionEngine {

    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler chainCompiler;
    private final IdGenerator idGenerator;

    public void execute(TaskDefinition def) {
        // 1. Cria a identidade da execução em background
        RequestIdentity identity = new RequestIdentity(
            idGenerator.generateFastId(),
            "BACKGROUND_" + def.nodeId().value(),
            "ORDER_BG",
            idGenerator.generateFastId(),
            Collections.emptySet()
        );

        // 2. Resolve a tarefa e compila a cadeia de interceptores
        Task coreTask = taskRegistry.getTask(def);
        Task executable = chainCompiler.compile(coreTask, def);

        try {
            // 3. Executa a tarefa com um contexto vazio (Shadow Context)
            TaskContext taskContext = new TaskContext(
                Collections.emptyMap(), 
                null, 
                def.nodeId().value(), 
                Set.of()
            );
            
            executable.execute(taskContext);
            
        } catch (Exception e) {
            log.error("Falha na task background [{}]: {}", def.nodeId().value(), e.getMessage());
        }
    }
}
