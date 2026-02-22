package br.com.orquestrator.orquestrator.core.engine.result;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.json.PathNavigator;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * DataPersistenceProcessor: Otimizado para Alocação Zero.
 */
@Component
@Order(2)
public class DataPersistenceProcessor implements TaskResultProcessor {

    @Override
    public void process(TaskResult result, TaskDefinition definition, ExecutionContext context) {
        final Object body = result.body();
        if (body == null) return;

        // Otimização: Processa as saídas declaradas (produces) sem criar chaves de sistema temporárias
        final List<DataSpec> produces = definition.getProduces();
        if (produces != null && !produces.isEmpty()) {
            for (int i = 0; i < produces.size(); i++) {
                DataSpec spec = produces.get(i);
                Object value = extractValue(body, spec.path());
                if (value != null) {
                    context.put(spec.name(), value);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object extractValue(Object body, String path) {
        if (path == null || path.isEmpty() || path.equals("$") || path.equals("root")) {
            return body;
        }
        
        if (body instanceof Map<?, ?> map) {
            return PathNavigator.read((Map<String, Object>) map, path);
        }
        
        return null;
    }
}
