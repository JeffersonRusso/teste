package br.com.orquestrator.orquestrator.tasks.tracing;

import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;

/**
 * Estratégia para extrair metadados específicos de um tipo de task para o trace.
 */
public interface MetadataExtractor {
    
    boolean supports(String taskType);
    
    void extract(TaskDefinition def, ExecutionSpan span);
}
