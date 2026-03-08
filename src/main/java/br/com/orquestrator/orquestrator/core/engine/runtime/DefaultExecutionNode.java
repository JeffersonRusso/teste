package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * DefaultExecutionNode: Maestro da execução de uma Task na DAG.
 * Otimizado: Usa templates pré-calculados para evitar alocações no caminho quente.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultExecutionNode implements ExecutionNode {

    private final String nodeId;
    private final Task executable;
    private final SignalProjector inputProjector;
    private final SignalProjector outputProjector;
    private final Set<String> requiredFields; // Pré-calculado no Assembler
    private ExecutionNode next;

    @Override
    public void run(SignalRegistry signals) {
        try {
            // 1. Gathering: Projeta sinais para a Task
            Map<String, DataValue> inputs = inputProjector.projectIn(signals);

            // 2. Execution: Executa a tarefa usando o Shadow Context
            // O nodeId e requiredFields são fixos, reduzindo o custo de criação do contexto
            TaskContext context = new TaskContext(inputs, null, nodeId, requiredFields);
            TaskResult result = executable.execute(context);

            // 3. Emission: Projeta o resultado de volta para o Registry
            if (result.isSuccess()) {
                outputProjector.projectOut(result.body(), signals);
            } else {
                throw new RuntimeException("Falha na execução da tarefa [" + nodeId + "]: " + result.status());
            }

            // 4. Continuation: Próximo nó na mesma thread (Zero Context Switch)
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
