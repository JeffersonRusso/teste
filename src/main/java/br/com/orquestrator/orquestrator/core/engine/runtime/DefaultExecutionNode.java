package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * DefaultExecutionNode: Implementação do nó de execução que respeita a malha de sinais.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultExecutionNode implements ExecutionNode {

    private final String nodeId;
    private final Task executable;
    private final List<String> inputSignals;
    private final List<String> outputSignals;

    @Override
    public void run(SignalRegistry signals) {
        try {
            // 1. Bloqueia até que todos os sinais de dependência sejam emitidos
            onSignal(signals);

            // 2. Executa a cadeia de tarefas (Decoradores + Core)
            TaskContext context = new TaskContext(Map.of(), null, nodeId);
            executable.execute(context);

        } finally {
            // 3. Libera os nós sucessores emitindo os sinais de saída
            emitSignal(signals);
        }
    }

    @Override
    public void onSignal(SignalRegistry signals) {
        if (inputSignals != null && !inputSignals.isEmpty()) {
            log.trace("Nó [{}] aguardando sinais de dependência: {}", nodeId, inputSignals);
            inputSignals.forEach(signals::await);
        }
    }

    @Override
    public void emitSignal(SignalRegistry signals) {
        if (outputSignals != null && !outputSignals.isEmpty()) {
            log.trace("Nó [{}] emitindo sinais de conclusão: {}", nodeId, outputSignals);
            outputSignals.forEach(signals::emit);
        }
    }

    @Override
    public String nodeId() {
        return nodeId;
    }
}
