package br.com.orquestrator.orquestrator.infra.health;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemHealthMonitor {

    private final MeterRegistry meterRegistry;
    private final Map<String, Integer> cutoffScores = new ConcurrentHashMap<>();

    // Limites de Latência (ms) - Poderiam vir de config por operação
    private static final long LATENCY_THRESHOLD_LOW = 200;
    private static final long LATENCY_THRESHOLD_HIGH = 1000;

    public int getCutoffScore(String operationType) {
        return cutoffScores.getOrDefault(operationType, 0);
    }

    @Scheduled(fixedRate = 5000)
    public void updateHealthStatus() {
        // Varre todos os timers de execução de pipeline
        for (Meter meter : meterRegistry.getMeters()) {
            if ("pipeline.execution.time".equals(meter.getId().getName())) {
                String operation = meter.getId().getTag("operation");
                if (operation != null && meter instanceof Timer timer) {
                    updateScoreForOperation(operation, timer);
                }
            }
        }
    }

    private void updateScoreForOperation(String operation, Timer timer) {
        if (timer.count() == 0) {
            cutoffScores.put(operation, 0);
            return;
        }

        double avgLatencyMs = timer.mean(TimeUnit.MILLISECONDS);
        int newScore = calculateCutoffScore(avgLatencyMs);
        
        Integer oldScore = cutoffScores.put(operation, newScore);
        
        if (oldScore == null || oldScore != newScore) {
            log.info("Health Update [{}]: Latency={}ms, Cutoff={}", operation, String.format("%.2f", avgLatencyMs), newScore);
        }
    }

    private int calculateCutoffScore(double latency) {
        if (latency < LATENCY_THRESHOLD_LOW) {
            return 0;
        } else if (latency < LATENCY_THRESHOLD_HIGH) {
            return 50;
        } else {
            return 80;
        }
    }
}
