package br.com.orquestrator.orquestrator.infra.listener;

import br.com.orquestrator.orquestrator.domain.event.PipelineFinishedEvent;
import br.com.orquestrator.orquestrator.infra.metrics.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsEventListener {

    private final MetricsService metricsService;

    @Async
    @EventListener
    public void onPipelineFinished(PipelineFinishedEvent event) {
        var summary = event.summary();
        String status = summary.success() ? "SUCCESS" : "ERROR";
        metricsService.recordPipelineExecution(summary.operationType(), status, summary.totalDurationMs());
    }
}
