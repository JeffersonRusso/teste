package br.com.orquestrator.orquestrator.tasks.base;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class FusedTask implements Task {

    private final List<Task> tasks;

    @Override
    public TaskResult execute(TaskContext context) {
        TaskResult lastResult = null;
        // Nota: Em uma fusão real, o contexto pode mudar entre as tasks (outputs de uma viram inputs da outra).
        // Mas como a fusão é feita no nível de executáveis já compilados (com seus decoradores),
        // cada task interna já tem sua própria cadeia de Input/Output.
        // O contexto passado aqui é o inicial do grupo.
        
        for (Task task : tasks) {
            lastResult = task.execute(context);
        }
        return lastResult;
    }
}
