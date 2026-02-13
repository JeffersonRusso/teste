//package br.com.orquestrator.orquestrator.service;
//
//import br.com.orquestrator.orquestrator.core.engine.DataFlowOrchestrator;
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import br.com.orquestrator.orquestrator.domain.event.PipelineFinishedEvent;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.event.EventListener;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.awaitility.Awaitility.await;
//
//@SpringBootTest
//class TraceEnrichmentTest {
//
//    @Autowired
//    private DataFlowOrchestrator orchestrator;
//
//    @Autowired
//    private TestEventListener eventListener;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @TestConfiguration
//    static class Config {
//        @Bean
//        public TestEventListener testEventListener() {
//            return new TestEventListener();
//        }
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    void shouldNotReflectMutationsInInputsTrace() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        Map<String, Object> complianceInfo = new HashMap<>();
//        complianceInfo.put("status", "APPROVED");
//        context.put("compliance_info", complianceInfo);
//
//        // Task que modifica o mapa in-place
//        TaskDefinition taskDef = new TaskDefinition(
//                "enrichCompliance",
//                "Enrich Compliance",
//                "SPEL",
//                1000,
//                objectMapper.valueToTree(Map.of("expression", "#compliance_info.put('isPilot', true) ?: #compliance_info")),
//                new FeaturePhases(null, null, null),
//                "refMutate"
//        );
//        taskDef.setRequires(List.of("compliance_info"));
//        taskDef.setProduces(List.of("compliance_enriched"));
//
//        // WHEN
//        orchestrator.run(context, List.of(taskDef));
//
//        // THEN
//        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
//            var event = eventListener.getReceivedEvents().getLast();
//            var node = event.summary().nodeMetrics().stream()
//                    .filter(m -> m.nodeId().equals("enrichCompliance"))
//                    .findFirst()
//                    .orElseThrow();
//
//            // INPUT não deve ter isPilot (foi clonado antes da mutação)
//            Map<String, Object> inputs = (Map<String, Object>) node.inputs().get("compliance_info");
//            assertThat(inputs).doesNotContainKey("isPilot");
//
//            // OUTPUT deve ter isPilot
//            Map<String, Object> outputs = (Map<String, Object>) node.outputs().get("compliance_enriched");
//            assertThat(outputs).containsKey("isPilot");
//        });
//    }
//
//    @Test
//    void shouldEnrichTraceWithInputsOutputsAndMetadata() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        context.put("inputVar", "inputValue");
//
//        TaskDefinition taskDef = new TaskDefinition(
//                "task1",
//                "Spel Task",
//                "SPEL",
//                1000,
//                objectMapper.valueToTree(Map.of("expression", "#inputVar + '_suffix'")),
//                new FeaturePhases(null, null, null),
//                "ref1"
//        );
//        taskDef.setRequires(List.of("inputVar"));
//        taskDef.setProduces(List.of("outputVar"));
//
//        // WHEN
//        orchestrator.run(context, List.of(taskDef));
//
//        // THEN
//        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
//            assertThat(eventListener.getReceivedEvents()).isNotEmpty();
//            var event = eventListener.getReceivedEvents().get(eventListener.getReceivedEvents().size() - 1);
//            var metrics = event.summary().nodeMetrics();
//
//            assertThat(metrics).hasSize(1);
//            var node = metrics.get(0);
//
//            assertThat(node.nodeId()).isEqualTo("task1");
//
//            // Verifica Inputs
//            assertThat(node.inputs()).containsEntry("inputVar", "inputValue");
//
//            // Verifica Outputs
//            assertThat(node.outputs()).containsEntry("outputVar", "inputValue_suffix");
//
//            // Verifica Metadata
//            assertThat(node.metadata()).containsEntry("expression", "#inputVar + '_suffix'");
//        });
//    }
//
//    @Test
//    void shouldEnrichGroovyTraceWithScriptMetadata() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        context.put("val", 10);
//
//        TaskDefinition taskDef = new TaskDefinition(
//                "groovy1",
//                "Groovy Task",
//                "GROOVY_SCRIPT",
//                1000,
//                objectMapper.valueToTree(Map.of("scriptBody", "return val * 2")),
//                new FeaturePhases(null, null, null),
//                "ref2"
//        );
//        taskDef.setRequires(List.of("val"));
//        taskDef.setProduces(List.of("result"));
//
//        // WHEN
//        orchestrator.run(context, List.of(taskDef));
//
//        // THEN
//        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
//            var event = eventListener.getReceivedEvents().get(eventListener.getReceivedEvents().size() - 1);
//            var metrics = event.summary().nodeMetrics();
//            var node = metrics.stream().filter(m -> m.nodeId().equals("groovy1")).findFirst().orElseThrow();
//
//            assertThat(node.metadata()).containsEntry("script", "return val * 2");
//            assertThat(node.inputs()).containsEntry("val", 10);
//            assertThat(node.outputs()).containsEntry("result", 20);
//        });
//    }
//
//    public static class TestEventListener {
//        private final List<PipelineFinishedEvent> receivedEvents = new CopyOnWriteArrayList<>();
//
//        @EventListener
//        public void onEvent(PipelineFinishedEvent event) {
//            receivedEvents.add(event);
//        }
//
//        public List<PipelineFinishedEvent> getReceivedEvents() {
//            return receivedEvents;
//        }
//    }
//}
