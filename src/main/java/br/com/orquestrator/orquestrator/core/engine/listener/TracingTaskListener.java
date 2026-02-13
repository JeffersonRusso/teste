package br.com.orquestrator.orquestrator.core.engine.listener;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.tracing.MetadataExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener de rastreamento de alta performance.
 * MDC removido completamente para eliminar overhead de CPU em Virtual Threads.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TracingTaskListener implements TaskExecutionListener {

    private final List<MetadataExtractor> metadataExtractors;

    @Override
    public void onStart(TaskDefinition taskDef, ExecutionContext context) {
        String nodeId = taskDef.getNodeId().value();
        
        ExecutionSpan span = context.getTracker().start(nodeId, taskDef.getType());
        context.put(ContextKey.SPAN_PREFIX + nodeId, span);

        captureInputs(taskDef, context, span);
        captureMetadata(taskDef, span);
    }

    @Override
    public void onSuccess(TaskDefinition taskDef, ExecutionContext context) {
        finishSpan(taskDef, context, null);
    }

    @Override
    public void onError(TaskDefinition taskDef, ExecutionContext context, Exception e) {
        finishSpan(taskDef, context, e);
    }

    private void finishSpan(TaskDefinition taskDef, ExecutionContext context, Exception error) {
        String nodeId = taskDef.getNodeId().value();
        String spanKey = ContextKey.SPAN_PREFIX + nodeId;
        Object spanObj = context.get(spanKey);
        
        if (!(spanObj instanceof ExecutionSpan span)) {
            return;
        }

        try (span) {
            if (error == null) {
                captureOutputs(taskDef, context, span);
                span.success();
            } else {
                if (error instanceof PipelineException pe) {
                    pe.getMetadata().forEach(span::addMetadata);
                }
                span.fail(error);
            }
        } finally {
            context.remove(spanKey);
        }
    }

    private void captureInputs(TaskDefinition def, ExecutionContext ctx, ExecutionSpan span) {
        if (def.getRequires() != null) {
            def.getRequires().forEach(req -> span.addInput(req.name(), ctx.get(req.name())));
        }
    }

    private void captureOutputs(TaskDefinition def, ExecutionContext ctx, ExecutionSpan span) {
        if (def.getProduces() != null) {
            def.getProduces().forEach(prod -> span.addOutput(prod.name(), ctx.get(prod.name())));
        }
    }

    private void captureMetadata(TaskDefinition def, ExecutionSpan span) {
        for (MetadataExtractor extractor : metadataExtractors) {
            if (extractor.supports(def.getType())) {
                extractor.extract(def, span);
                return;
            }
        }
    }
}
