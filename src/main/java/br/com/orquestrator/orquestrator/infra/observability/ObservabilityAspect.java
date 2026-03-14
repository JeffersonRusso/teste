package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEvent;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObservabilityAspect implements ObservationHandler<RequestIdentity> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onStart(RequestIdentity context) {
        eventPublisher.publishEvent(new PipelineEvent.PipelineStarted(context, Map.of()));
    }

    @Override public void onStop(RequestIdentity context) { /* Gerido pelo UseCase */ }

    @Override
    public void onError(RequestIdentity context) {
        eventPublisher.publishEvent(new PipelineEvent.PipelineFinished(context, Map.of(), false));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof RequestIdentity;
    }
}
