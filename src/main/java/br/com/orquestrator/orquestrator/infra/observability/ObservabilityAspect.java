package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ObservabilityAspect {

    private final PipelineEventPublisher eventPublisher;

    @Before("execution(* br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession.execute(..))")
    public void beforeExecute() {
        if (ContextHolder.getContext().isPresent()) {
            eventPublisher.publishPipelineStarted(ContextHolder.metadata());
        }
    }

    @AfterReturning("execution(* br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession.execute(..))")
    public void afterExecuteSuccess() {
        if (ContextHolder.getContext().isPresent()) {
            eventPublisher.publishPipelineFinished(ContextHolder.metadata(), true);
        }
    }

    @AfterThrowing("execution(* br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession.execute(..))")
    public void afterExecuteFailure() {
        if (ContextHolder.getContext().isPresent()) {
            eventPublisher.publishPipelineFinished(ContextHolder.metadata(), false);
        }
    }
}
