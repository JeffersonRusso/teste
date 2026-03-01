package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskResultMapper;
import br.com.orquestrator.orquestrator.core.engine.validation.GuardEvaluator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRunner {

    private final TaskValidator validator;
    private final GuardEvaluator guardEvaluator;
    private final TaskResultMapper resultMapper;

    public void run(Pipeline.TaskNode node) {
        ExecutionContext context = ContextHolder.CONTEXT.get();

        ScopedValue.where(ContextHolder.CURRENT_NODE, node.nodeId()).run(() -> {
            ThreadContext.put("nodeId", node.nodeId());
            long startTime = System.currentTimeMillis();

            try {
                validator.validate(node, context);
                if (!guardEvaluator.shouldRun(node.guardCondition(), context)) return;

                log.debug(">>> [{}]", node.nodeId());
                
                // EXECUÇÃO SEM PARÂMETRO: O contexto flui via ScopedValue
                TaskResult result = node.executable().execute();

                if (result != null) {
                    resultMapper.map(node, result, context);
                    log.debug("<<< [{}] {}ms", node.nodeId(), System.currentTimeMillis() - startTime);
                }
            } catch (Exception e) {
                handleFailure(node, e, context);
            } finally {
                ThreadContext.remove("nodeId");
            }
        });
    }

    private void handleFailure(Pipeline.TaskNode node, Exception e, ExecutionContext context) {
        log.error("!!! [{}] FALHA: {}", node.nodeId(), e.getMessage());
        context.put(node.nodeId() + ".error", e.getMessage());
        if (node.failFast()) throw (e instanceof RuntimeException re) ? re : new PipelineException(e.getMessage(), e);
    }
}
