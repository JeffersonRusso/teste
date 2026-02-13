package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsável por estimar o tempo total de execução de um pipeline
 * calculando o Caminho Crítico (Critical Path) do grafo de dependências.
 */
@Component
public class PipelineTimeoutCalculator {

    private final double marginPercentage;

    public PipelineTimeoutCalculator(@Value("${orquestrator.pipeline.timeout.margin-percentage:0.10}") double marginPercentage) {
        this.marginPercentage = marginPercentage;
    }

    public Duration calculate(List<TaskDefinition> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Duration.ZERO;
        }

        Map<String, TaskDefinition> taskMap = tasks.stream()
                .collect(Collectors.toMap(t -> t.getNodeId().value(), t -> t));
        
        Map<String, String> producerMap = buildProducerMap(tasks);
        Map<String, Long> memo = new HashMap<>();
        
        long maxPath = 0;

        for (TaskDefinition task : tasks) {
            maxPath = Math.max(maxPath, getPathTime(task, taskMap, producerMap, memo));
        }
        
        // Aplica a margem percentual (ex: 10%)
        long finalTimeout = (long) (maxPath * (1.0 + marginPercentage));
        
        return Duration.ofMillis(finalTimeout);
    }

    private Map<String, String> buildProducerMap(List<TaskDefinition> tasks) {
        Map<String, String> producerMap = new HashMap<>();
        for (TaskDefinition task : tasks) {
            if (task.getProduces() != null) {
                task.getProduces().forEach(p -> producerMap.put(p.name(), task.getNodeId().value()));
            }
        }
        return producerMap;
    }

    private long getPathTime(TaskDefinition current, Map<String, TaskDefinition> taskMap, 
                             Map<String, String> producerMap, Map<String, Long> memo) {
        String nodeId = current.getNodeId().value();
        if (memo.containsKey(nodeId)) {
            return memo.get(nodeId);
        }

        long dependenciesMaxTime = 0;
        
        if (current.getRequires() != null) {
            for (DataSpec req : current.getRequires()) {
                String producerId = resolveProducerId(req.name(), producerMap);
                
                if (producerId != null && !producerId.equals(nodeId)) {
                    TaskDefinition producer = taskMap.get(producerId);
                    if (producer != null) {
                        dependenciesMaxTime = Math.max(dependenciesMaxTime, getPathTime(producer, taskMap, producerMap, memo));
                    }
                }
            }
        }

        long totalTime = dependenciesMaxTime + current.getTimeoutMs();
        memo.put(nodeId, totalTime);
        return totalTime;
    }
    
    private String resolveProducerId(String key, Map<String, String> producerMap) {
        String id = producerMap.get(key);
        if (id != null) return id;
        
        // Tenta resolver aninhado (ex: cliente.endereco -> cliente)
        int dotIndex = key.indexOf('.');
        if (dotIndex > 0) {
            return producerMap.get(key.substring(0, dotIndex));
        }
        return null;
    }
}
