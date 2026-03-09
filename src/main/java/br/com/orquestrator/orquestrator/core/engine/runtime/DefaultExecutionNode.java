package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * DefaultExecutionNode: Maestro da execução de uma Task na DAG.
 * Agora usa JsonNode puro.
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
        try {
            // 1. Gathering
            Map<String, JsonNode> inputs = inputProjector.projectIn(signals);

            // 2. Execution
            TaskResult result = executable.execute(inputs);

            // 3. Emission
            if (result.isSuccess()) {
                outputProjector.projectOut(result.body(), signals);
            } else {
                throw new RuntimeException("Falha na execução da tarefa [" + nodeId + "]: " + result.status());
            }

            // 4. Continuation
            if (next != null) next.run(signals);

        } catch (Exception e) {
            outputProjector.fail(signals, e);
            throw e;
        }
    }

    @Override public void then(ExecutionNode next) { this.next = next; }
    @Override public Map<String, JsonNode> onSignal(SignalRegistry signals) { return inputProjector.projectIn(signals); }
    @Override public void emitSignal(SignalRegistry signals, JsonNode resultBody) { outputProjector.projectOut(resultBody, signals); }
    @Override public String nodeId() { return nodeId; }
}
