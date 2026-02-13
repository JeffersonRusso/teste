package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.s3.S3Task;
import br.com.orquestrator.orquestrator.tasks.s3.S3TaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3TaskProvider implements TaskProvider {

    private final ObjectMapper objectMapper;
    private final ExpressionService expressionService;

    @Override
    public String getType() {
        return "S3_EXPORT";
    }

    @Override
    public Task create(TaskDefinition def) {
        // 1. Extração e Tradução: O Provider resolve a "sujeira" do JSON
        S3TaskConfiguration config = parseConfiguration(def.getConfig(), def.getNodeId().value());
        
        // 2. Instanciação Limpa: A S3Task recebe o que precisa, já mastigado
        return new S3Task(def, objectMapper, expressionService, config);
    }

    private S3TaskConfiguration parseConfiguration(JsonNode json, String nodeId) {
        if (json == null || json.isMissingNode()) {
            throw new TaskConfigurationException("Configuração S3 ausente para a task: " + nodeId);
        }

        try {
            // Java 21: Conversão segura do JSON para o Record de configuração
            return objectMapper.treeToValue(json, S3TaskConfiguration.class);
        } catch (JsonProcessingException e) {
            throw new TaskConfigurationException("Erro ao processar configuração S3 para a task: " + nodeId, e);
        }
    }
}
