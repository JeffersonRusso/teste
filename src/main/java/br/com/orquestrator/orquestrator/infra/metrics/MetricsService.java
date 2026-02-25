//package br.com.orquestrator.orquestrator.infra.metrics;
//
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Timer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//public class MetricsService {
//
//    private final MeterRegistry registry;
//
//    public void recordPipelineExecution(String operationType, String status, long durationMs) {
//        Timer.builder("pipeline.execution.time")
//                .tag("operation", operationType)
//                .tag("status", status)
//                .register(registry)
//                .record(durationMs, TimeUnit.MILLISECONDS);
//
//        Counter.builder("pipeline.execution.count")
//                .tag("operation", operationType)
//                .tag("status", status)
//                .register(registry)
//                .increment();
//    }
//
//    public void recordTaskExecution(String taskType, String nodeId, String status, long durationMs) {
//        Timer.builder("task.execution.time")
//                .tag("type", taskType)
//                .tag("nodeId", nodeId)
//                .tag("status", status)
//                .register(registry)
//                .record(durationMs, TimeUnit.MILLISECONDS);
//    }
//}
