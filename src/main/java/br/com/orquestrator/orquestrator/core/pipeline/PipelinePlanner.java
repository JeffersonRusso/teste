package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * PipelinePlanner: Organiza tasks em camadas paralelas de forma declarativa.
 */
@Component
public class PipelinePlanner {

    public List<List<TaskDefinition>> plan(List<TaskDefinition> tasks, Set<String> initialKeys) {
        var layers = new ArrayList<List<TaskDefinition>>();
        var available = new HashSet<>(initialKeys);
        var remaining = new ArrayList<>(tasks);

        while (!remaining.isEmpty()) {
            // 1. Filtra as tasks que já possuem todos os requisitos satisfeitos
            var ready = remaining.stream()
                    .filter(t -> isReady(t, available))
                    .toList();

            if (ready.isEmpty()) {
                layers.add(List.copyOf(remaining)); // Deadlock: joga o resto na última camada
                break;
            }

            // 2. Registra a nova camada e atualiza o estado de chaves conhecidas
            layers.add(ready);
            remaining.removeAll(ready);
            ready.forEach(t -> available.addAll(getProducedKeys(t)));
        }
        return layers;
    }

    private boolean isReady(TaskDefinition task, Set<String> available) {
        if (task.getRequires() == null) return true;
        
        return task.getRequires().stream()
                .filter(req -> !req.optional())
                .allMatch(req -> isAvailable(req.name(), available));
    }

    private boolean isAvailable(String key, Set<String> available) {
        // Uma chave está disponível se ela existe ou se seu "pai" existe (dot notation)
        return available.contains(key) || 
               available.stream().anyMatch(k -> key.startsWith(STR."\{k}."));
    }

    private Set<String> getProducedKeys(TaskDefinition task) {
        var keys = new HashSet<String>();
        keys.add(task.getNodeId().value());
        if (task.getProduces() != null) {
            task.getProduces().forEach(p -> keys.add(p.name()));
        }
        return keys;
    }
}
