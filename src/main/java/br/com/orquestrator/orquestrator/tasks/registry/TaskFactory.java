package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registro central de tasks instanciadas e prontas para execução.
 * Atua como o "Cofre" de tasks, garantindo que a instância correta (ID + Versão) seja entregue.
 * Java 21: Utiliza String Templates e SequencedCollections para gestão de cache.
 */
@Slf4j
@Service
public class TaskFactory implements TaskRegistry {

    private final TaskBuilder taskBuilder;
    
    // Chave composta (ID:Versão) para suportar Canary Releases e Hot Reload com segurança
    private volatile Map<String, Task> taskCache = Collections.emptyMap();

    public TaskFactory(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;
    }

    @Override
    public Task getTask(TaskDefinition def) {
        // Java 21: String Template para gerar a chave composta
        String cacheKey = STR."\{def.getNodeId().value()}:\{def.getVersion()}";
        
        Task task = taskCache.get(cacheKey);
        if (task != null) {
            return task;
        }
        
        // Fallback: Criação sob demanda (Cache Miss)
        log.warn("Cache Miss para Task [{}]. Criando instância sob demanda.", cacheKey);
        return createNewTask(def);
    }
    
    @Override
    public Task createNewTask(TaskDefinition def) {
        // A validação agora é responsabilidade dos Providers/Builder
        return taskBuilder.build(def);
    }

    @Override
    public void refreshRegistry(Map<NodeId, Task> newTasks) {
        // Transformamos o mapa recebido para usar a chave composta (ID:Versão)
        Map<String, Task> versionedCache = newTasks.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> STR."\{entry.getKey().value()}:1", // Fallback para v1 se não houver info
                        Map.Entry::getValue
                ));

        // Troca atômica da referência para garantir thread-safety total
        this.taskCache = versionedCache;
        log.info("Catálogo sincronizado. {} tasks prontas para execução.", versionedCache.size());
    }

    /**
     * Atualiza o registro com tasks que já possuem versão explícita.
     */
    public void refreshVersionedRegistry(Map<String, Task> versionedTasks) {
        this.taskCache = Map.copyOf(versionedTasks);
        log.info("Catálogo versionado sincronizado. {} tasks prontas.", versionedTasks.size());
    }
    
    @Override
    public void clearRegistry() {
        this.taskCache = Collections.emptyMap();
        log.info("Cache de tasks limpo.");
    }
}
