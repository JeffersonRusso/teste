package br.com.orquestrator.orquestrator.core.engine.flow;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.core.engine.runtime.LazySignalMap;
import br.com.orquestrator.orquestrator.core.engine.runtime.SignalRegistry;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.model.vo.DataBinding;
import br.com.orquestrator.orquestrator.domain.model.vo.NodeId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ExecutionNode: Unidade de execução auto-contida.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE) // Construtor privado força o uso da fábrica
public final class ExecutionNode {

    private final TaskDefinition definition;
    private final Task compiledTask; 
    private final Map<String, DataBinding> inputMappings;
    private final Map<String, DataBinding> outputMappings;
    private final DataFactory dataFactory;

    /**
     * Fábrica de montagem do nó (Static Factory Method).
     * Lei de Deméter: Isola o compilador da complexidade de montagem técnica do nó.
     */
    public static ExecutionNode from(TaskDefinition def, TaskRegistry registry, DataFactory factory) {
        return new ExecutionNode(
            def,
            registry.getCompiledTask(def), // Busca a tarefa já com a pilha de interceptores
            def.getExecutionInputBindings(),
            def.getExecutionOutputBindings(),
            factory
        );
    }

    public void run(SignalRegistry signals) {
        Map<String, DataNode> inputs = new LazySignalMap(signals, inputMappings, dataFactory);
        TaskExecutionContext context = new TaskExecutionContext(definition, inputs);
        
        TaskResult result = compiledTask.execute(context);
        
        handleResult(result, signals);
    }

    private void handleResult(TaskResult result, SignalRegistry signals) {
        switch (result) {
            case TaskResult.Success s -> projectOutputs(s.body(), signals);
            case TaskResult.Failure f -> failOutputs(signals, new RuntimeException(f.errorMessage()));
            case TaskResult.Skipped s -> projectOutputs(dataFactory.missing(), signals);
        }
    }

    private void projectOutputs(DataNode result, SignalRegistry signals) {
        outputMappings.forEach((localKey, binding) -> {
            DataNode val = (localKey == null || localKey.isBlank()) ? result : result.find(localKey);
            signals.emit(binding.signal(), val);
        });
    }

    private void failOutputs(SignalRegistry signals, Throwable cause) {
        outputMappings.values().forEach(binding -> signals.fail(binding.signal(), cause));
    }

    public NodeId nodeId() { return definition.nodeId(); }
}
