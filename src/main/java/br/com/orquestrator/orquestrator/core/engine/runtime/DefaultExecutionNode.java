package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * DefaultExecutionNode: Maestro da execução de uma Task na DAG.
 * Instrumentado para diagnóstico de latência.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultExecutionNode implements ExecutionNode {

    private final String nodeId;
    private final Task executable;
    private final SignalProjector inputProjector;
    private final SignalProjector outputProjector;
    private final Set<String> requiredFields;
    private ExecutionNode next;

    @Override
    public void run(SignalRegistry signals) {
        long startNode = System.nanoTime();
        try {
            // 1. Gathering
            long startGather = System.nanoTime();
            Map<String, DataValue> inputs = inputProjector.projectIn(signals);
            long durationGather = System.nanoTime() - startGather;

            // 2. Execution
            long startExec = System.nanoTime();
            TaskResult result = executable.execute(inputs);
            long durationExec = System.nanoTime() - startExec;

            // 3. Emission
            long startEmit = System.nanoTime();
            if (result.isSuccess()) {
                outputProjector.projectOut(result.body(), signals);
            } else {
                throw new RuntimeException("Falha na execução da tarefa [" + nodeId + "]: " + result.status());
            }
            long durationEmit = System.nanoTime() - startEmit;

            long totalNode = System.nanoTime() - startNode;
            
            // Loga o breakdown do tempo
            log.error("[PROFILER] Node: {} | Total: {}ms | Gather: {}ms | Exec: {}ms | Emit: {}ms", 
                nodeId, 
                totalNode / 1_000_000.0, 
                durationGather / 1_000_000.0, 
                durationExec / 1_000_000.0, 
                durationEmit / 1_000_000.0
            );

            // 4. Continuation
            if (next != null) next.run(signals);

        } catch (Exception e) {
            outputProjector.fail(signals, e);
            throw e;
        }
    }

    @Override public void then(ExecutionNode next) { this.next = next; }
    @Override public Map<String, DataValue> onSignal(SignalRegistry signals) { return inputProjector.projectIn(signals); }
    @Override public void emitSignal(SignalRegistry signals, DataValue resultBody) { outputProjector.projectOut(resultBody, signals); }
    @Override public String nodeId() { return nodeId; }
}
