package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.application.usecase.PipelineExecutionResult;
import br.com.orquestrator.orquestrator.core.context.OrquestratorContext;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.ports.input.ExecutePipelineUseCase;
import br.com.orquestrator.orquestrator.core.ports.input.command.ExecutionCommand;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * AnalysisController: Adaptador Web oficial.
 */
@Slf4j
@RestController
@RequestMapping("/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final ExecutePipelineUseCase pipelineUseCase;
    private final ObservationRegistry observationRegistry;
    private final IdGenerator idGenerator;

    public record AnalysisRequest(
        String operationType,
        String orderId,
        Map<String, Object> operation
    ) {
        public String executionStrategy() { return (String) operation.get("_strategy"); }
    }

    @PostMapping
    public ResponseEntity<PipelineExecutionResult> analyze(@RequestBody AnalysisRequest request) {
        RequestIdentity identity = createIdentity(request);

        ExecutionCommand command = mapToCommand(request, identity);

        PipelineExecutionResult result = OrquestratorContext.runWith(identity, () -> 
            Observation.createNotStarted("pipeline.execution", () -> identity, observationRegistry)
                .observe(() -> pipelineUseCase.execute(command))
        );

        return result.success() ? ResponseEntity.ok(result) : ResponseEntity.internalServerError().body(result);
    }

    private RequestIdentity createIdentity(AnalysisRequest request) {
        return new RequestIdentity(
            idGenerator.generateFastId(),
            request.operationType(),
            request.orderId(),
            idGenerator.generateFastId(),
            Set.of()
        );
    }

    private ExecutionCommand mapToCommand(AnalysisRequest request, RequestIdentity identity) {
        return new ExecutionCommand(
            request.operationType(),
            identity.getCorrelationId(),
            request.executionStrategy(),
            request.operation()
        );
    }
}
