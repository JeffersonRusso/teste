//package br.com.orquestrator.orquestrator.task;
//
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import br.com.orquestrator.orquestrator.infra.el.ExpressionEvaluator;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.tasks.script.SpelTask;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class SpelTaskTest {
//
//    private ExpressionEvaluator evaluator;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        evaluator = new ExpressionEvaluator();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void shouldEvaluateSpelAndPutResultInContext() {
//        // GIVEN
//        JsonNode config = objectMapper.valueToTree(Map.of("expression", "#input_val + 10"));
//        TaskDefinition def = new TaskDefinition("task1", "Task 1", "SPEL", 1000, config, null, "ref1");
//        def.setProduces(List.of("output_val"));
//
//        SpelTask task = new SpelTask(def);
//
//        ExecutionContext context = new ExecutionContext(evaluator);
//        context.put("input_val", 5);
//
//        // WHEN
//        task.execute(context);
//
//        // THEN
//        assertThat(context.get("output_val")).isEqualTo(15);
//        assertThat(context.get("node_task1_status")).isEqualTo(200);
//    }
//
//    @Test
//    void shouldAddValueToMapAndReturnMap() {
//        // GIVENaaa
//        Map<String, Object> complianceInfo = new HashMap<>();
//        complianceInfo.put("id", "123");
//
//        // Expression trick: (put returns null, so ?: returns the map)
//        JsonNode config = objectMapper.valueToTree(Map.of("expression", "#compliance_info.put('isPilot', true) ?: #compliance_info"));
//        TaskDefinition def = new TaskDefinition("enrich", "Enrich", "SPEL", 1000, config, null, "ref2");
//        def.setProduces(List.of("compliance_enriched"));
//
//        SpelTask task = new SpelTask(def);
//
//        ExecutionContext context = new ExecutionContext(evaluator);
//        context.put("compliance_info", complianceInfo);
//
//        // WHEN
//        task.execute(context);
//
//        // THEN
//        Map<String, Object> result = (Map<String, Object>) context.get("compliance_enriched");
//        assertThat(result).isNotNull();
//        assertThat(result.get("id")).isEqualTo("123");
//        assertThat(result.get("isPilot")).isEqualTo(true);
//    }
//}
