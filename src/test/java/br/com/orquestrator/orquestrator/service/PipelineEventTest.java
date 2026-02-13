//package br.com.orquestrator.orquestrator.service;
//
//import br.com.orquestrator.orquestrator.core.engine.DataFlowOrchestrator;
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import br.com.orquestrator.orquestrator.domain.event.PipelineFinishedEvent;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.event.EventListener;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.awaitility.Awaitility.await;
//
//@SpringBootTest
//class PipelineEventTest {
//
//    @Autowired
//    private DataFlowOrchestrator orchestrator;
//
//    @Autowired
//    private TestEventListener eventListener;
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
//    void shouldPublishEventWhenPipelineFinishes() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        List<TaskDefinition> tasks = List.of(); // Pipeline vazio é o mais simples para o teste
//
//        // WHEN
//        orchestrator.run(context, tasks);
//
//        // THEN
//        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
//            assertThat(eventListener.getReceivedEvents()).hasSize(1);
//            var event = eventListener.getReceivedEvents().get(0);
//            assertThat(event.summary().correlationId()).isEqualTo(context.getCorrelationId());
//            assertThat(event.summary().success()).isTrue();
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
