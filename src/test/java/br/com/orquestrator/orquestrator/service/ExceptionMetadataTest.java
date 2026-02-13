//package br.com.orquestrator.orquestrator.service;
//
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import br.com.orquestrator.orquestrator.exception.PipelineException;
//import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.tasks.base.Task;
//import br.com.orquestrator.orquestrator.tasks.registry.TaskFactory;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@SpringBootTest
//class ExceptionMetadataTest {
//
//    @Autowired
//    private TaskFactory taskFactory;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void shouldPopulateNodeIdAndMetadataInPipelineExceptionFromHttpTask() {
//        // GIVEN: Uma task HTTP com URL inválida para forçar erro
//        TaskDefinition taskDef = new TaskDefinition(
//                "clientEndpoint",
//                "Get Client",
//                "HTTP",
//                1000,
//                objectMapper.valueToTree(Map.of("method", "GET", "url", "http://invalid-url-that-does-not-exist-123.com")),
//                new FeaturePhases(null, null, null),
//                "refClient"
//        );
//
//        Task task = taskFactory.create(taskDef);
//        ExecutionContext context = new ExecutionContext();
//
//        // WHEN / THEN
//        assertThatThrownBy(() -> task.execute(context))
//                .isInstanceOf(PipelineException.class)
//                .satisfies(e -> {
//                    PipelineException pe = (PipelineException) e;
//                    assertThat(pe.getNodeId()).isEqualTo("clientEndpoint");
//                    assertThat(pe.getMetadata()).containsEntry("url", "http://invalid-url-that-does-not-exist-123.com");
//                    assertThat(pe.getMetadata()).containsEntry("method", "GET");
//                });
//    }
//
//    @Test
//    void shouldPopulateMetadataInSpelTaskError() {
//        // GIVEN: Uma task SPEL que vai falhar (divisão por zero)
//        TaskDefinition taskDef = new TaskDefinition(
//                "mathTask",
//                "Divide Task",
//                "SPEL",
//                1000,
//                objectMapper.valueToTree(Map.of("expression", "1 / 0")),
//                new FeaturePhases(null, null, null),
//                "refMath"
//        );
//
//        Task task = taskFactory.create(taskDef);
//        ExecutionContext context = new ExecutionContext();
//
//        // WHEN / THEN
//        assertThatThrownBy(() -> task.execute(context))
//                .isInstanceOf(PipelineException.class)
//                .satisfies(e -> {
//                    PipelineException pe = (PipelineException) e;
//                    assertThat(pe.getNodeId()).isEqualTo("mathTask");
//                    assertThat(pe.getMetadata()).containsEntry("expression", "1 / 0");
//                });
//
//        // Verificação adicional: Metadados da exceção devem estar no ExecutionTracker
//        var metrics = context.getTracker().getMetrics();
//        var nodeMetrics = metrics.stream().filter(m -> m.nodeId().equals("mathTask")).findFirst().get();
//        assertThat(nodeMetrics.metadata()).containsEntry("expression", "1 / 0");
//    }
//
//    @Test
//    void shouldIgnoreErrorBasedOnNodeId() {
//        // GIVEN: Uma task que falha mas tem ErrorHandler configurado para ignorar esse nodeId
//        Map<String, Object> errorHandlerConfig = Map.of(
//                "action", "CONTINUE",
//                "ignoreNodes", List.of("clientEndpoint"),
//                "fallbackValue", "DEFAULT_CLIENT"
//        );
//
//        TaskDefinition taskDef = new TaskDefinition(
//                "clientEndpoint",
//                "Get Client",
//                "HTTP",
//                1000,
//                objectMapper.valueToTree(Map.of("method", "GET", "url", "http://invalid-url-error")),
//                new FeaturePhases(List.of(new br.com.orquestrator.orquestrator.domain.FeatureDefinition("ERROR_HANDLER", null, objectMapper.valueToTree(errorHandlerConfig))), null, null),
//                "refClient"
//        );
//        taskDef.setProduces(List.of("client_info"));
//
//        Task task = taskFactory.create(taskDef);
//        ExecutionContext context = new ExecutionContext();
//
//        // WHEN: Executa (não deve lançar exception pois foi ignorado)
//        task.execute(context);
//
//        // THEN: Variável de output deve ter o fallback
//        assertThat(context.get("client_info").toString()).contains("DEFAULT_CLIENT");
//        assertThat(context.get("node_clientEndpoint_error")).asString().contains("ERROR_IGNORED");
//    }
//}
