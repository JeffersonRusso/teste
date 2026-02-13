//package br.com.orquestrator.orquestrator.tasks.interceptor;
//
//import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
//import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class S3MetricsInterceptorTest {
//
//    @Mock
//    private S3MetricsInterceptor self;
//
//    @Mock
//    private TaskChain chain;
//
//    @InjectMocks
//    private S3MetricsInterceptor interceptor;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    void shouldCallSelfExportAsyncAfterProceed() {
//        // GIVEN
//        ExecutionContext context = new ExecutionContext();
//        FeatureDefinition config = new FeatureDefinition("S3_EXPORT", null, objectMapper.createObjectNode());
//        TaskDefinition taskDef = new TaskDefinition(
//                "testNode", "Test Name", "HTTP", 1000, objectMapper.createObjectNode(), null, "ref123"
//        );
//
//        when(chain.proceed(context)).thenReturn(context);
//
//        // WHEN
//        interceptor.intercept(context, chain, config, taskDef);
//
//        // THEN
//        verify(chain, times(1)).proceed(context);
//        verify(self, times(1)).exportAsync(eq(context), eq(taskDef));
//    }
//}
