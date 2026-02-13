package br.com.orquestrator.orquestrator.core.context.init;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Order(0)
@RequiredArgsConstructor
public class TimeoutContextInitializer implements ContextInitializer {

    // Timeout global de segurança (fallback) caso o cálculo dinâmico falhe ou demore muito para ser calculado
    // Este é um "hard limit" inicial, mas o PipelineAssembler pode refinar isso depois.
    @Value("${app.pipeline.global-timeout-ms:60000}")
    private long globalTimeoutMs;

    @Override
    public void initialize(ExecutionContext context, String operationType) {
        // Define um deadline inicial generoso.
        // O PipelineAssembler calculará o timeout real baseado no grafo e atualizará o deadline se necessário,
        // ou o DataFlowOrchestrator usará o timeout do objeto Pipeline.
        context.setDeadline(Instant.now().plusMillis(globalTimeoutMs));
    }
}
