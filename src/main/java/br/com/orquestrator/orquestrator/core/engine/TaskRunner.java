package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Executor de Task: Roda a lógica e pluga o resultado na árvore JSON.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRunner {

    private final TaskRegistry taskRegistry;

    public void run(TaskDefinition definition, ExecutionContext context) {
        String nodeId = definition.getNodeId().value();

        ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId)
                .run(() -> {
                    try {
                        Task task = taskRegistry.getTask(definition);
                        Object result = task.execute(context);
                        
                        if (result != null) {
                            // 1. Pluga o resultado bruto na árvore
                            context.put(nodeId, result);
                            
                            // 2. Processa Aliases (produces) de forma nativa na árvore
                            List<DataSpec> produces = definition.getProduces();
                            if (produces != null) {
                                for (DataSpec spec : produces) {
                                    // Resolve o dado usando o caminho nativo da árvore
                                    String sourcePath = (spec.path() == null || spec.path().isBlank()) 
                                            ? nodeId 
                                            : STR."\{nodeId}.\{spec.path()}";
                                    
                                    JsonNode val = context.get(sourcePath);
                                    if (val != null) context.put(spec.name(), val);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Falha no nó [{}]: {}", nodeId, e.getMessage());
                        if (definition.isFailFast()) {
                            throw (e instanceof RuntimeException re) ? re : new PipelineException(e.getMessage(), e);
                        }
                    }
                });
    }
}
