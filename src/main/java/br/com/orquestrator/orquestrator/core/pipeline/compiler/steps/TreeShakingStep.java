package br.com.orquestrator.orquestrator.core.pipeline.compiler.steps;

import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TreeShakingStep: Otimiza o grafo de execução removendo tasks desnecessárias.
 */
@Slf4j
@Component
public class TreeShakingStep implements PipelineCompilationStep {

    @Override
    public Stream<TaskDefinition> execute(PipelineDefinition definition, Stream<TaskDefinition> tasks, Set<String> activeTags) {
        Set<String> requiredOutputs = definition.defaultRequiredOutputs();
        if (requiredOutputs == null || requiredOutputs.isEmpty()) return Stream.empty();

        List<TaskDefinition> taskList = tasks.collect(Collectors.toList());

        // Mapeia quem produz o quê (Usa o atalho da TaskDefinition)
        Map<String, TaskDefinition> signalProducers = new HashMap<>();
        for (TaskDefinition task : taskList) {
            task.getProducedSignalNames().forEach(signal -> 
                signalProducers.put(signal, task)
            );
        }

        Set<TaskDefinition> tasksToKeep = new HashSet<>();
        Queue<String> signalsToResolve = new LinkedList<>(requiredOutputs);
        Set<String> resolvedSignals = new HashSet<>();

        while (!signalsToResolve.isEmpty()) {
            String signal = signalsToResolve.poll();
            if (resolvedSignals.contains(signal) || "raw".equals(signal)) continue;
            resolvedSignals.add(signal);

            TaskDefinition producer = signalProducers.get(signal);
            if (producer != null) {
                tasksToKeep.add(producer);
                // CORREÇÃO DEMÉTER: Pede apenas os nomes dos sinais requeridos.
                signalsToResolve.addAll(producer.getRequiredSignalNames());
            }
        }

        log.info("Tree Shaking [{}]: Reduzido de {} para {} tasks.", 
                definition.operationType(), taskList.size(), tasksToKeep.size());

        return taskList.stream().filter(tasksToKeep::contains);
    }
}
