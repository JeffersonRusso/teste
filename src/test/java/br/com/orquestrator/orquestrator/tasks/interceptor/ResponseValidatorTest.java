//package br.com.orquestrator.orquestrator.tasks.interceptor;
//
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
//import br.com.orquestrator.orquestrator.exception.PipelineException;
//import br.com.orquestrator.orquestrator.adapter.persistence.repository.CustomErrorRepository;
//import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.CustomErrorEntity;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@Transactional
//class ResponseValidatorTest {
//
//    @Autowired
//    private ResponseValidatorInterceptor interceptor;
//
//    @Autowired
//    private CustomErrorRepository errorRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setup() {
//        errorRepository.save(new CustomErrorEntity("ERR_TEST", "Erro no nó #{#node_id} com status #{#node_status}"));
//        errorRepository.save(new CustomErrorEntity("ERR_SIMPLE", "Mensagem simples do banco"));
//    }
//
//    @Test
//    void shouldThrowExceptionWithInlineMessage() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        context.put("status_code", 400);
//
//        TaskDefinition taskDef = mock(TaskDefinition.class);
//        when(taskDef.getNodeId()).thenReturn("task1");
//
//        FeatureDefinition config = new FeatureDefinition("RESPONSE_VALIDATOR", null, objectMapper.valueToTree(Map.of(
//            "rules", List.of(
//                Map.of(
//                    "condition", "#status_code == 400",
//                    "message", "Falha detectada: #{#status_code}"
//                )
//            )
//        )));
//
//        TaskChain chain = mock(TaskChain.class);
//        when(chain.proceed(any())).thenReturn(context);
//
//        // WHEN & THEN
//        PipelineException ex = assertThrows(PipelineException.class, () ->
//            interceptor.intercept(context, chain, config, taskDef)
//        );
//
//        assertEquals("Falha detectada: 400", ex.getMessage());
//        assertEquals("task1", ex.getNodeId());
//    }
//
//    @Test
//    void shouldThrowExceptionUsingErrorBank() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        context.put("node_task1_status", 500);
//
//        TaskDefinition taskDef = mock(TaskDefinition.class);
//        when(taskDef.getNodeId()).thenReturn("task1");
//
//        FeatureDefinition config = new FeatureDefinition("RESPONSE_VALIDATOR", null, objectMapper.valueToTree(Map.of(
//            "rules", List.of(
//                Map.of(
//                    "condition", "#node_task1_status == 500",
//                    "errorCode", "ERR_TEST"
//                )
//            )
//        )));
//
//        TaskChain chain = mock(TaskChain.class);
//        when(chain.proceed(any())).thenReturn(context);
//
//        // WHEN & THEN
//        PipelineException ex = assertThrows(PipelineException.class, () ->
//            interceptor.intercept(context, chain, config, taskDef)
//        );
//
//        // Verifica se a interpolação usando as variáveis locais (node_id, node_status) funcionou
//        assertEquals("Erro no nó task1 com status 500", ex.getMessage());
//    }
//
//    @Test
//    void shouldNotThrowExceptionIfConditionIsFalse() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        context.put("status_code", 200);
//
//        TaskDefinition taskDef = mock(TaskDefinition.class);
//        when(taskDef.getNodeId()).thenReturn("task1");
//
//        FeatureDefinition config = new FeatureDefinition("RESPONSE_VALIDATOR", null, objectMapper.valueToTree(Map.of(
//            "rules", List.of(
//                Map.of(
//                    "condition", "#status_code != 200",
//                    "message", "Erro"
//                )
//            )
//        )));
//
//        TaskChain chain = mock(TaskChain.class);
//        when(chain.proceed(any())).thenReturn(context);
//
//        // WHEN
//        ExecutionContext result = interceptor.intercept(context, chain, config, taskDef);
//
//        // THEN
//        assertNotNull(result);
//        verify(chain).proceed(context);
//    }
//
//    @Test
//    void shouldIncludeExtraMetadataInException() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//
//        TaskDefinition taskDef = mock(TaskDefinition.class);
//        when(taskDef.getNodeId()).thenReturn("task1");
//
//        FeatureDefinition config = new FeatureDefinition("RESPONSE_VALIDATOR", null, objectMapper.valueToTree(Map.of(
//            "rules", List.of(
//                Map.of(
//                    "condition", "true",
//                    "message", "Erro",
//                    "metadata", Map.of("severity", "CRITICAL", "code", "999")
//                )
//            )
//        )));
//
//        TaskChain chain = mock(TaskChain.class);
//        when(chain.proceed(any())).thenReturn(context);
//
//        // WHEN
//        PipelineException ex = assertThrows(PipelineException.class, () ->
//            interceptor.intercept(context, chain, config, taskDef)
//        );
//
//        // THEN
//        assertEquals("CRITICAL", ex.getMetadata().get("severity"));
//        assertEquals("999", ex.getMetadata().get("code"));
//    }
//}
