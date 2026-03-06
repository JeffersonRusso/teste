package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ResultExtractor {

    public Map<String, Object> extract(ReadableContext reader, Pipeline pipeline) {
        Set<DataPath> outputs = pipeline.requiredOutputs();
        
        if (outputs == null || outputs.isEmpty()) return Map.of();

        Map<String, Object> result = new HashMap<>((int) (outputs.size() / 0.75f) + 1);
        
        for (DataPath path : outputs) {
            // CAMINHO QUENTE: Usa o DataPath pré-resolvido
            DataValue dv = reader.get(path);
            
            if (!(dv instanceof DataValue.Empty)) {
                result.put(path.value(), dv.raw());
            }
        }

        return result;
    }
}
