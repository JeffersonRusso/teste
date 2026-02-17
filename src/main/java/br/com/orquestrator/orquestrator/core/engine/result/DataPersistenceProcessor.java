package br.com.orquestrator.orquestrator.core.engine.result;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.json.PathNavigator;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DataPersistenceProcessor: Especialista em salvar resultados no contexto.
 * Agora padronizado para usar caminhos (paths) em todas as extrações.
 */
@Component
@Order(2)
public class DataPersistenceProcessor implements TaskResultProcessor {

    @Override
    public void process(TaskResult result, TaskDefinition definition, ExecutionContext context) {
        if (result.body() == null) return;

        String nodeId = definition.getNodeId().value();
        
        // 1. Salva o resultado bruto da task em um local temporário de sistema
        String internalKey = STR."sys.tasks.\{nodeId}.raw";
        context.put(internalKey, result.body());

        // 2. Processa as saídas declaradas (produces)
        if (definition.getProduces() != null) {
            for (DataSpec spec : definition.getProduces()) {
                Object value = extractValue(result.body(), spec.path());
                if (value != null) {
                    context.put(spec.name(), value);
                }
            }
        }
    }

    private Object extractValue(Object body, String path) {
        if (path == null || path.equals("$") || path.equals("root")) {
            return body;
        }
        
        // Se o body for um Map, usamos o PathNavigator para extrair o campo
        if (body instanceof Map<?, ?> map) {
            return PathNavigator.read((Map<String, Object>) map, path);
        }
        
        return null;
    }
}
