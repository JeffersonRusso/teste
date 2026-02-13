package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineValidator {

    private final TaskCatalogProvider taskProvider;

    public void validate(@NonNull final Pipeline pipeline, @NonNull final ExecutionContext context) {
        validateIntegrity(pipeline, context);
        validateCycles(pipeline);
        log.debug("Pipeline validado com sucesso. {} tasks.", pipeline.size());
    }

    private void validateIntegrity(final Pipeline pipeline, final ExecutionContext context) {
        final Set<String> availableKeys = collectAvailableKeys(pipeline, context);

        for (TaskDefinition task : pipeline.getTasks()) {
            validateTaskRequirements(task, availableKeys);
        }
    }

    private Set<String> collectAvailableKeys(final Pipeline pipeline, final ExecutionContext context) {
        final Set<String> keys = new HashSet<>(context.getDataStore().keySet());

        // Coleta chaves de tasks globais e do pipeline atual
        Stream.concat(
            taskProvider.findAllActive().stream().filter(TaskDefinition::isGlobal),
            pipeline.getTasks().stream()
        )
        .filter(t -> t.getProduces() != null)
        .flatMap(t -> t.getProduces().stream())
        .map(DataSpec::name)
        .forEach(keys::add);

        return keys;
    }

    private void validateTaskRequirements(final TaskDefinition task, final Set<String> availableKeys) {
        if (task.getRequires() == null) return;

        for (DataSpec spec : task.getRequires()) {
            if (spec.optional()) continue;

            if (!availableKeys.contains(spec.name()) && !isNestedKeyAvailable(spec.name(), availableKeys)) {
                throw new PipelineValidationException(
                    String.format("Task '%s' requer '%s' mas ninguém produz essa chave. Chaves disponíveis: %s", 
                            task.getNodeId(), spec.name(), availableKeys)
                ).withNodeId(task.getNodeId().value());
            }
        }
    }

    private boolean isNestedKeyAvailable(final String requiredKey, final Set<String> availableKeys) {
        final int dotIndex = requiredKey.indexOf('.');
        if (dotIndex > 0) {
            final String rootKey = requiredKey.substring(0, dotIndex);
            return availableKeys.contains(rootKey);
        }
        return false;
    }

    private void validateCycles(final Pipeline pipeline) {
        final Map<String, List<String>> adjList = buildDependencyGraph(pipeline);
        detectCycles(adjList);
    }

    private Map<String, List<String>> buildDependencyGraph(final Pipeline pipeline) {
        // Mapa de Produção: Output -> TaskId
        final Map<String, String> producerMap = pipeline.getTasks().stream()
                .filter(t -> t.getProduces() != null)
                .flatMap(t -> t.getProduces().stream()
                        .map(p -> Map.entry(p.name(), t.getNodeId().value())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        // Grafo de Dependência: TaskId -> List<DependencyTaskId>
        return pipeline.getTasks().stream()
                .collect(Collectors.toMap(
                        t -> t.getNodeId().value(),
                        t -> getDependencies(t, producerMap)
                ));
    }

    private List<String> getDependencies(final TaskDefinition task, final Map<String, String> producerMap) {
        if (task.getRequires() == null) return Collections.emptyList();
        
        return task.getRequires().stream()
                .map(req -> producerMap.get(req.name()))
                .filter(Objects::nonNull)
                .filter(producerId -> !producerId.equals(task.getNodeId().value()))
                .toList();
    }

    private void detectCycles(final Map<String, List<String>> adjList) {
        final Set<String> visited = new HashSet<>();
        final Set<String> recursionStack = new HashSet<>();

        for (String taskId : adjList.keySet()) {
            if (hasCycle(taskId, adjList, visited, recursionStack)) {
                throw new PipelineValidationException("Ciclo de dependência detectado no pipeline envolvendo a task: " + taskId);
            }
        }
    }

    private boolean hasCycle(final String current, final Map<String, List<String>> adj, 
                             final Set<String> visited, final Set<String> stack) {
        if (stack.contains(current)) return true;
        if (visited.contains(current)) return false;

        visited.add(current);
        stack.add(current);

        for (String neighbor : adj.getOrDefault(current, Collections.emptyList())) {
            if (hasCycle(neighbor, adj, visited, stack)) return true;
        }

        stack.remove(current);
        return false;
    }
}
