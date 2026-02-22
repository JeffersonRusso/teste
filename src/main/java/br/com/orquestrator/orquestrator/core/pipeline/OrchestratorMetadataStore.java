package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OrchestratorMetadataStore: A "Única Fonte da Verdade" em memória.
 * Centraliza todos os metadados necessários para a orquestração.
 */
@Slf4j
@Component
public class OrchestratorMetadataStore {

    private final Map<String, TaskDefinition> tasks = new ConcurrentHashMap<>();
    private final Map<String, FlowDefinition> flows = new ConcurrentHashMap<>();

    public void updateTasks(List<TaskDefinition> newTasks) {
        log.info("Atualizando cache de Tasks: {} instâncias", newTasks.size());
        tasks.clear();
        newTasks.forEach(t -> tasks.put(t.getNodeId().value(), t));
    }

    public void updateFlows(List<FlowDefinition> newFlows) {
        log.info("Atualizando cache de Fluxos: {} instâncias", newFlows.size());
        flows.clear();
        newFlows.forEach(f -> flows.put(f.operationType(), f));
    }

    public Optional<TaskDefinition> getTask(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    public List<TaskDefinition> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    public Optional<FlowDefinition> getFlow(String operationType) {
        return Optional.ofNullable(flows.get(operationType));
    }
}
