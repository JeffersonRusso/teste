package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final ExecutionSession executionSession;
    private final ObservationRegistry observationRegistry;
    private final IdGenerator idGenerator;

    public record AnalysisRequest(
        String operationType,
        String orderId,
        Map<String, Object> operation
    ) {}

    @PostMapping
    public Map<String, Object> analyze(@RequestBody AnalysisRequest request) {
        RequestIdentity identity = new RequestIdentity(
            idGenerator.generateFastId(),
            request.operationType(),
            request.orderId(),
            idGenerator.generateFastId(),
            Set.of()
        );

        log.info("Iniciando análise. CorrelationId: {} | ExecutionId: {} | Operation: {}",
                identity.getCorrelationId(), identity.getExecutionId(), identity.getOperationType());

        return Observation.createNotStarted("pipeline.execution", () -> identity, observationRegistry)
                .observe(() -> executionSession.execute(identity, request.operation()));
    }
}
