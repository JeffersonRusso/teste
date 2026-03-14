package br.com.orquestrator.orquestrator.application.usecase;

import br.com.orquestrator.orquestrator.core.context.OrquestratorContext;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEvent;
import br.com.orquestrator.orquestrator.core.engine.strategy.ExecutionStrategy;
import br.com.orquestrator.orquestrator.core.engine.strategy.ExecutionStrategySelector;
import br.com.orquestrator.orquestrator.core.ports.input.ExecutePipelineUseCase;
import br.com.orquestrator.orquestrator.core.ports.input.command.ExecutionCommand;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService implements ExecutePipelineUseCase {

    private final PipelineLoader pipelineLoader; // Novo carregador com cache
    private final ExecutionStrategySelector strategySelector;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PipelineExecutionResult execute(ExecutionCommand command) {
        long startTime = System.currentTimeMillis();
        var context = OrquestratorContext.get();
        
        try {
            // 1. Carregamento via loader cacheado
            Pipeline pipeline = pipelineLoader.load(command.operationType(), context.getActiveTags());
            
            // 2. Seleção
            ExecutionStrategy engine = strategySelector.select(
                command.executionStrategy() != null ? command.executionStrategy() : pipeline.executionStrategy()
            );
            
            // 3. Execução
            Map<String, Object> output = engine.execute(pipeline, command.payload());
            
            long duration = System.currentTimeMillis() - startTime;
            eventPublisher.publishEvent(new PipelineEvent.PipelineFinished(context, output, true));
            
            return PipelineExecutionResult.success(context.getExecutionId(), command.operationType(), output, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Falha no pipeline [{}]: {}", command.operationType(), e.getMessage());
            eventPublisher.publishEvent(new PipelineEvent.PipelineFinished(context, Map.of(), false));
            return PipelineExecutionResult.failure(context.getExecutionId(), command.operationType(), e.getMessage(), duration);
        }
    }
}
