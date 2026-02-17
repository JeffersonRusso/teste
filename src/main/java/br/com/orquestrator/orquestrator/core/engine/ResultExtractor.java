package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * ResultExtractor: Especialista em extrair os dados finais do contexto.
 */
@Component
public class ResultExtractor {

    public Map<String, Object> extract(ExecutionContext context, Pipeline pipeline) {
        return pipeline.requiredOutputs().stream()
                .filter(key -> context.get(key) != null)
                .collect(Collectors.toMap(
                        key -> key,
                        context::get,
                        (existing, _) -> existing
                ));
    }
}
