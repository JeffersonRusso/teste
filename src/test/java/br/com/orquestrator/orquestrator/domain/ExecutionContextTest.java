//package br.com.orquestrator.orquestrator.domain;
//
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import org.junit.jupiter.api.Test;
//import java.util.Map;
//import static org.junit.jupiter.api.Assertions.*;
//
//class ExecutionContextTest {
//
//    @Test
//    void shouldStoreAndRetrieveData() {
//        ExecutionContext context = new ExecutionContext();
//        context.put("testKey", "testValue");
//        assertEquals("testValue", context.get("testKey"));
//    }
//
//    @Test
//    void shouldEvaluateSpELExpression() {
//        ExecutionContext context = new ExecutionContext();
//        context.put("standard", Map.of("valor", 100));
//
//        Boolean result = context.evaluate("#standard.valor > 50", Boolean.class);
//        assertTrue(result);
//    }
//
//    @Test
//    void shouldResolveTemplateExpressions() {
//        ExecutionContext context = new ExecutionContext();
//        context.put("cliente", Map.of("nome", "Jeffe"));
//
//        String resolved = context.resolveExpression("Olá #{#cliente.nome}");
//        assertEquals("Olá Jeffe", resolved);
//    }
//
//    @Test
//    void shouldHandleMapAccessorInSpEL() {
//        ExecutionContext context = new ExecutionContext();
//        context.put("data", Map.of("nested", Map.of("value", 42)));
//
//        Integer result = context.evaluate("#data.nested.value", Integer.class);
//        assertEquals(42, result);
//    }
//
//    @Test
//    void shouldTrackExecution() {
//        ExecutionContext context = new ExecutionContext();
//        assertNotNull(context.getTracker());
//    }
//}
