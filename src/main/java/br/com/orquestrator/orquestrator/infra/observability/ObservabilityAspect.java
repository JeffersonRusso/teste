package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * ObservabilityAspect: Captura eventos de ciclo de vida do pipeline para telemetria.
 * Agora desacoplado do ContextHolder e focado na RequestIdentity.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ObservabilityAspect {

    private final PipelineEventPublisher eventPublisher;

    @Before("execution(* br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession.run(..))")
    public void beforeExecute(JoinPoint joinPoint) {
        findIdentity(joinPoint).ifPresent(eventPublisher::publishPipelineStarted);
    }

    @AfterReturning("execution(* br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession.run(..))")
    public void afterExecuteSuccess(JoinPoint joinPoint) {
        findIdentity(joinPoint).ifPresent(id -> eventPublisher.publishPipelineFinished(id, true));
    }

    @AfterThrowing("execution(* br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession.run(..))")
    public void afterExecuteFailure(JoinPoint joinPoint) {
        findIdentity(joinPoint).ifPresent(id -> eventPublisher.publishPipelineFinished(id, false));
    }

    private java.util.Optional<RequestIdentity> findIdentity(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof RequestIdentity)
                .map(arg -> (RequestIdentity) arg)
                .findFirst();
    }
}
