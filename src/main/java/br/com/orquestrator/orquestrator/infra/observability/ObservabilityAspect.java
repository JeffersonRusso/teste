package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObservabilityAspect implements ObservationHandler<RequestIdentity> {

    private final PipelineEventPublisher eventPublisher;

    @Override
    public void onStart(RequestIdentity context) {
        eventPublisher.publishPipelineStart(context, Map.of());
    }

    @Override
    public void onStop(RequestIdentity context) {
        eventPublisher.publishPipelineFinished(context, Map.of(), true);
    }

    @Override
    public void onError(RequestIdentity context) {
        eventPublisher.publishPipelineFinished(context, Map.of(), false);
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof RequestIdentity;
    }
}
