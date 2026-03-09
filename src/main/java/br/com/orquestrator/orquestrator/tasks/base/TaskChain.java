package br.com.orquestrator.orquestrator.tasks.base;

import java.util.LinkedList;
import java.util.List;

/**
 * OBSOLETO: A lógica de cadeia agora é feita via InterceptorTask e RealInterceptorChain.
 */
public class TaskChain implements Task {

    private final List<Task> tasks = new LinkedList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public TaskResult execute(java.util.Map<String, com.fasterxml.jackson.databind.JsonNode> inputs) {
        TaskResult lastResult = null;
        for (Task task : tasks) {
            lastResult = task.execute(inputs);
        }
        return lastResult;
    }
}
