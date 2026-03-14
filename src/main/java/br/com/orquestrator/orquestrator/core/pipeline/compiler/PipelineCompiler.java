package br.com.orquestrator.orquestrator.core.pipeline.compiler;

import br.com.orquestrator.orquestrator.core.engine.flow.ExecutionNode;
import br.com.orquestrator.orquestrator.core.pipeline.compiler.steps.PipelineCompilationStep;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.model.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.core.engine.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PipelineCompiler: Compilador de pipelines purificado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineCompiler {

    private final List<PipelineCompilationStep> steps;
    private final TaskRegistry taskRegistry;
    private final DataFactory dataFactory;

    public Pipeline compile(PipelineDefinition definition, Set<String> activeTags) {
        log.info("Iniciando compilação do pipeline: {} (v{})", definition.operationType(), definition.version());

        Stream<TaskDefinition> taskStream = definition.tasks().stream();

        // 1. Executa os passos de compilação
        for (PipelineCompilationStep step : steps) {
            taskStream = step.execute(definition, taskStream, activeTags);
        }

        // 2. Constrói o grafo de execução delegando a montagem para o próprio nó
        // TELL, DON'T ASK: O compilador apenas fornece as peças, o nó se monta.
        Map<NodeId, ExecutionNode> nodes = taskStream
                .collect(Collectors.toMap(
                    TaskDefinition::nodeId,
                    def -> ExecutionNode.from(def, taskRegistry, dataFactory)
                ));

        return new Pipeline(
                nodes,
                Duration.ofMillis(definition.timeoutMs()),
                definition.defaultRequiredOutputs(),
                definition.executionStrategy()
        );
    }
}
