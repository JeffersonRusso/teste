package br.com.orquestrator.orquestrator.core.engine.registry;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.interceptor.InterceptorStackFactory;
import br.com.orquestrator.orquestrator.core.ports.output.TaskProvider;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TaskRegistry: Gerencia o ciclo de vida das tasks.
 * Utiliza descoberta nativa via nomes de Bean do Spring.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistry {

    // Spring injeta um mapa onde a chave é o nome no @Component("NOME")
    private final Map<String, TaskProvider> providers; 
    private final InterceptorStackFactory stackFactory;
    private final Map<String, Task> compiledCache = new ConcurrentHashMap<>();

    public Task getCompiledTask(TaskDefinition def) {
        return compiledCache.computeIfAbsent(def.nodeId().value(), k -> compile(def));
    }

    private Task compile(TaskDefinition def) {
        log.debug("Compilando task: {} [{}]", def.nodeId(), def.type());
        
        // Busca direta pelo nome do Bean (em maiúsculo para tolerância)
        TaskProvider provider = providers.get(def.type().toUpperCase());
        if (provider == null) {
            throw new IllegalArgumentException("Nenhum provedor encontrado para o tipo: " + def.type());
        }

        Task coreTask = provider.create(def);
        return stackFactory.assemble(coreTask, def);
    }
}
