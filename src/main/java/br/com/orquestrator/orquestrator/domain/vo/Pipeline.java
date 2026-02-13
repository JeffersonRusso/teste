package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.Getter;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Getter
public class Pipeline {

    private final List<TaskDefinition> tasks;
    private final Duration timeout;
    private final Set<String> requiredOutputs;

    public Pipeline(List<TaskDefinition> tasks, Duration timeout, Set<String> requiredOutputs) {
        this.tasks = (tasks != null) ? List.copyOf(tasks) : Collections.emptyList();
        this.timeout = (timeout != null) ? timeout : Duration.ofMinutes(1);
        this.requiredOutputs = (requiredOutputs != null) ? Set.copyOf(requiredOutputs) : Set.of("resultado_final");
    }

    public int size() {
        return tasks.size();
    }
    
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
    
    public Stream<TaskDefinition> stream() {
        return tasks.stream();
    }
}
