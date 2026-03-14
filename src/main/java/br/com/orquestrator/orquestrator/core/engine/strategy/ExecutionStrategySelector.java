package br.com.orquestrator.orquestrator.core.engine.strategy;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ExecutionStrategySelector: Fábrica que escolhe a estratégia de execução correta.
 */
@Component
public class ExecutionStrategySelector {

    private final Map<String, ExecutionStrategy> strategies;

    public ExecutionStrategySelector(List<ExecutionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toUnmodifiableMap(
                    s -> s.getType().toUpperCase(), 
                    Function.identity()
                ));
    }

    public ExecutionStrategy select(String strategyType) {
        // Default para ASYNC (Virtual Threads) se não especificado
        String type = (strategyType == null || strategyType.isBlank()) ? "ASYNC" : strategyType.toUpperCase();
        
        ExecutionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new PipelineException("Estratégia de execução desconhecida: " + type);
        }
        return strategy;
    }
}
