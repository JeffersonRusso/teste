package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishFinished(ExecutionContext context, boolean success) {
        log.info("Pipeline finalizado: {} | Sucesso: {}", context.getOperationType(), success);
        // Aqui você pode disparar eventos reais do Spring se necessário
    }
}
