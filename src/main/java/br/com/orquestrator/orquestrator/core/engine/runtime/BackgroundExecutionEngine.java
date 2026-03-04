package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextFactory;
import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * BackgroundExecutionEngine: Executa tarefas isoladas fora de um pipeline.
 * Garante que a tarefa tenha acesso a um escopo soberano efêmero.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackgroundExecutionEngine {

    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler chainCompiler;
    private final ContextFactory contextFactory;

    public void execute(TaskDefinition def) {
        log.info("Iniciando execução em background da task: [{}]", def.nodeId().value());

        // 1. Cria um contexto efêmero para a execução
        ExecutionContext context = contextFactory.create("BACKGROUND_" + def.nodeId().value(), Map.of(), Map.of());

        // 2. Compila a tarefa com toda a sua resiliência (Retry, Cache, etc)
        Task coreTask = taskRegistry.getTask(def);
        Task executable = chainCompiler.compile(coreTask, def);

        // 3. Executa dentro de um escopo soberano
        ScopedValue.where(ContextHolder.CONTEXT, context).run(() -> {
            try {
                executable.execute();
                log.info("Execução em background da task [{}] concluída com sucesso.", def.nodeId().value());
            } catch (Exception e) {
                log.error("Falha na execução em background da task [{}]: {}", def.nodeId().value(), e.getMessage());
            }
        });
    }
}
