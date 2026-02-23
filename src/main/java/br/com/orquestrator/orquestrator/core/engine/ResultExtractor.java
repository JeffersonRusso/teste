package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ResultExtractor: Especialista em extrair os dados finais do contexto.
 * Otimizado para Zero-Stream no Hot Path.
 */
@Component
public class ResultExtractor {

    public Map<String, Object> extract(ExecutionContext context, Pipeline pipeline) {
        Set<String> outputs = pipeline.requiredOutputs();
        if (outputs == null || outputs.isEmpty()) return Map.of();

        // OTIMIZAÇÃO: Pré-dimensionar o HashMap para evitar redimensionamento (rehashing)
        Map<String, Object> result = new HashMap<>((int) (outputs.size() / 0.75f) + 1);

        for (String key : outputs) {
            Object value = context.get(key);
            if (value != null) {
                result.put(key, value);
            }
        }

        return result;
    }
}
