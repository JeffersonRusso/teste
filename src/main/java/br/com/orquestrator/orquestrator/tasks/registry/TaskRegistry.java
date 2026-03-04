package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskRegistry: Gerencia o ciclo de vida e o cache das instâncias de tarefas.
 * Garante que cada definição de nó resulte em uma única instância executável.
 */
@Slf4j
@Component
public class TaskRegistry {

    private final Map<String, TaskProvider> providers;
    
    // Cache de instâncias: Chave é o NodeId para garantir unicidade por nó
    private final Map<String, Task> instanceCache = new ConcurrentHashMap<>(1024);

    public TaskRegistry(List<TaskProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toUnmodifiableMap(
                    p -> p.getType().toUpperCase(), 
                    Function.identity()
                ));
        log.info("TaskRegistry inicializado com {} provedores: {}", providers.size(), providers.stream().map(TaskProvider::getType).toList());
    }

    /**
     * Recupera ou cria uma instância de tarefa baseada na sua definição.
     */
    public Task getTask(TaskDefinition def) {
        String nodeId = def.nodeId().value();
        
        return instanceCache.computeIfAbsent(nodeId, id -> {
            log.debug("Criando nova instância de task para o nó: [{}] (Tipo: {})", id, def.type());
            return createInstance(def);
        });
    }

    private Task createInstance(TaskDefinition def) {
        String type = def.type().toUpperCase();
        TaskProvider provider = providers.get(type);
        
        if (provider == null) {
            throw new PipelineException(String.format(
                "Falha na criação da task [%s]: Provedor para o tipo '%s' não encontrado.", 
                def.nodeId().value(), type));
        }

        try {
            return provider.create(def);
        } catch (Exception e) {
            throw new PipelineException(String.format(
                "Erro ao instanciar task [%s] do tipo '%s': %s", 
                def.nodeId().value(), type, e.getMessage()), e);
        }
    }

    /**
     * Limpa o cache de instâncias (Útil para reload de pipelines).
     */
    public void clear() {
        instanceCache.clear();
        log.info("Cache de instâncias de tasks limpo.");
    }
}
