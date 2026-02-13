package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskGraphFilter {

    /**
     * Filtra a lista de tasks mantendo apenas aquelas necessárias para produzir os outputs desejados
     * (Backward Chaining).
     */
    public List<TaskDefinition> filterByDependencies(@NonNull final List<TaskDefinition> tasks, @NonNull final Set<String> finalOutputKeys) {
        final Map<String, TaskDefinition> producerMap = buildProducerMap(tasks);
        final Set<TaskDefinition> necessaryTasks = identifyNecessaryTasks(producerMap, finalOutputKeys);
        
        return pruneList(tasks, necessaryTasks);
    }

    private Map<String, TaskDefinition> buildProducerMap(final List<TaskDefinition> tasks) {
        return tasks.stream()
                .filter(t -> t.getProduces() != null)
                .flatMap(t -> t.getProduces().stream().map(p -> Map.entry(p.name(), t)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }

    private Set<TaskDefinition> identifyNecessaryTasks(final Map<String, TaskDefinition> producerMap, final Set<String> finalOutputKeys) {
        final Set<TaskDefinition> necessaryTasks = new HashSet<>();
        final Queue<String> keysToResolve = new LinkedList<>(finalOutputKeys);
        final Set<String> resolvedKeys = new HashSet<>();

        while (!keysToResolve.isEmpty()) {
            final String key = keysToResolve.poll();
            if (!resolvedKeys.add(key)) continue;

            processKey(key, producerMap, necessaryTasks, keysToResolve);
            processNestedKey(key, resolvedKeys, keysToResolve);
        }
        return necessaryTasks;
    }

    private void processKey(final String key, final Map<String, TaskDefinition> producerMap, 
                            final Set<TaskDefinition> necessaryTasks, final Queue<String> keysToResolve) {
        final TaskDefinition producer = producerMap.get(key);
        
        if (producer != null && necessaryTasks.add(producer)) {
            if (producer.getRequires() != null) {
                producer.getRequires().forEach(req -> keysToResolve.add(req.name()));
            }
        }
    }

    private void processNestedKey(final String key, final Set<String> resolvedKeys, final Queue<String> keysToResolve) {
        if (key.contains(".")) {
            final String rootKey = key.split("\\.")[0];
            if (!resolvedKeys.contains(rootKey)) {
                keysToResolve.add(rootKey);
            }
        }
    }

    private List<TaskDefinition> pruneList(final List<TaskDefinition> tasks, final Set<TaskDefinition> necessaryTasks) {
        final List<TaskDefinition> optimizedList = tasks.stream()
                .filter(t -> t.isGlobal() || necessaryTasks.contains(t))
                .toList();

        if (optimizedList.size() < tasks.size()) {
            log.info("Grafo filtrado: {} tasks removidas. (Original: {}, Final: {})", 
                    tasks.size() - optimizedList.size(), tasks.size(), optimizedList.size());
        }

        return optimizedList;
    }
}
