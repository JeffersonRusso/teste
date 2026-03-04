package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ResultExtractor: Extrai os dados finais do banco de contexto.
 * Recebe a visão de leitura explicitamente (Desacoplado do escopo).
 */
@Component
public class ResultExtractor {

    public Map<String, Object> extract(ReadableContext reader, Pipeline pipeline) {
        Set<String> outputs = pipeline.requiredOutputs();
        if (outputs == null || outputs.isEmpty()) return Map.of();

        Map<String, Object> result = new HashMap<>((int) (outputs.size() / 0.75f) + 1);
        for (String key : outputs) {
            Object value = reader.get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }
}
