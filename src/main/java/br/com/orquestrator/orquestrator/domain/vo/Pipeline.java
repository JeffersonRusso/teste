package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.Getter;

import java.time.Duration;
import java.util.*;

/**
 * Pipeline: O Plano de Voo Baseado em Contrato.
 */
@Getter
public class Pipeline {

    private final List<TaskDefinition> tasks;
    private final Duration timeout;
    private final Set<String> requiredOutputs;
    private final List<List<TaskDefinition>> layers;

    public Pipeline(List<TaskDefinition> allTasks, 
                    Duration timeout, 
                    Set<String> requiredOutputs, 
                    Set<String> initialKeys) {
        this.timeout = (timeout != null) ? timeout : Duration.ofMinutes(1);
        this.requiredOutputs = Set.copyOf(requiredOutputs);
        
        // 1. Organiza em camadas baseado no contrato
        this.layers = plan(allTasks, initialKeys);
        this.tasks = allTasks;
    }

    private List<List<TaskDefinition>> plan(List<TaskDefinition> tasks, Set<String> initialKeys) {
        List<List<TaskDefinition>> layers = new ArrayList<>();
        Set<String> known = new HashSet<>(initialKeys);
        List<TaskDefinition> remaining = new ArrayList<>(tasks);

        while (!remaining.isEmpty()) {
            List<TaskDefinition> layer = new ArrayList<>();
            for (Iterator<TaskDefinition> it = remaining.iterator(); it.hasNext(); ) {
                TaskDefinition t = it.next();
                if (canRun(t, known)) {
                    layer.add(t);
                    it.remove();
                }
            }
            if (layer.isEmpty()) {
                // Se não conseguimos progredir, jogamos o restante em uma camada final
                layers.add(new ArrayList<>(remaining));
                break;
            }
            layer.forEach(t -> {
                known.add(t.getNodeId().value());
                if (t.getProduces() != null) t.getProduces().forEach(p -> known.add(p.name()));
            });
            layers.add(layer);
        }
        return layers;
    }

    private boolean canRun(TaskDefinition t, Set<String> known) {
        if (t.getRequires() == null || t.getRequires().isEmpty()) return true;
        return t.getRequires().stream()
                .filter(req -> !req.optional())
                .allMatch(req -> isKeyAvailable(req.name(), known));
    }

    private boolean isKeyAvailable(String key, Set<String> known) {
        if (known.contains(key)) return true;
        String current = key;
        while (current.contains(".")) {
            current = current.substring(0, current.lastIndexOf('.'));
            if (known.contains(current)) return true;
        }
        return false;
    }
}
