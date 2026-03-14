package br.com.orquestrator.orquestrator.core.engine.strategy;

import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import java.util.Map;

/**
 * ExecutionStrategy: Define COMO um pipeline deve ser executado.
 */
public interface ExecutionStrategy {
    
    String getType();

    Map<String, Object> execute(Pipeline pipeline, Map<String, Object> input);
}
