package br.com.orquestrator.orquestrator.tasks.core;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * NormalizationTask: Transforma dados brutos em sinais normalizados.
 * Agora opera diretamente com JsonNode.
 */
@RequiredArgsConstructor
public class NormalizationTask implements Task {

    private final Map<String, String> rules;
    private final ObjectMapper objectMapper;

    @Override
    public TaskResult execute(Map<String, JsonNode> inputs) {
        ObjectNode result = objectMapper.createObjectNode();
        
        // O input principal é o sinal 'raw'
        JsonNode inputRoot = inputs.get("raw");
        if (inputRoot == null) {
            return TaskResult.success(result);
        }

        rules.forEach((targetKey, sourcePath) -> {
            // Converte o path para JsonPointer
            String pointer = sourcePath.startsWith("/") ? sourcePath : "/" + sourcePath.replace('.', '/');
            JsonNode value = inputRoot.at(pointer);
            
            if (!value.isMissingNode()) {
                // Adiciona o valor extraído no novo objeto JSON
                // Para chaves aninhadas (ex: "customer.id"), precisaríamos de uma lógica mais complexa.
                // Assumindo que o targetKey é plano por enquanto.
                result.set(targetKey, value);
            }
        });

        return TaskResult.success(result);
    }
}
