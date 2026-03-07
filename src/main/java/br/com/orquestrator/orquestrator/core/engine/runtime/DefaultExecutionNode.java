package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

            // 2. Executa a cadeia de tarefas
            TaskContext context = new TaskContext(Map.of(), null, nodeId, Set.of());
            executable.execute(context);

            // 3. Sucesso: Libera os nós sucessores
            emitSignal(signals);

        } catch (Exception e) {
            // 4. Falha: Avisa os sucessores para não esperarem (Fail-Fast)
            failSignal(signals, e);
            throw e;
        }
    }

    @Override
    public void onSignal(SignalRegistry signals) {
        if (inputSignals != null) {
            inputSignals.forEach(signals::await);
        }
    }

    @Override
    public void emitSignal(SignalRegistry signals) {
        if (outputSignals != null) {
            outputSignals.forEach(signals::emit);
        }
    }

    /**
     * Propaga a falha para os sinais de saída.
     */
    private void failSignal(SignalRegistry signals, Throwable cause) {
        if (outputSignals != null) {
            outputSignals.forEach(s -> signals.fail(s, cause));
        }
    }

    @Override public String nodeId() { return nodeId; }
}
