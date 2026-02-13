package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.json.MapBuilder;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import br.com.orquestrator.orquestrator.tasks.script.dmn.DmnTask;
import br.com.orquestrator.orquestrator.tasks.script.dmn.DmnTaskConfiguration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DmnTaskProvider implements TaskProvider {

    private final DmnEngine dmnEngine;
    private final TaskResultMapper resultMapper;
    private final ObjectMapper objectMapper;
    private final MapBuilder mapBuilder;

    @Override
    public String getType() {
        return "DMN";
    }

    @Override
    public Task create(TaskDefinition def) {
        DmnTaskConfiguration config = parseConfiguration(def);
        return new DmnTask(def, dmnEngine, resultMapper, objectMapper, mapBuilder, config);
    }

    private DmnTaskConfiguration parseConfiguration(TaskDefinition def) {
        JsonNode json = def.getConfig();
        if (json == null || json.isMissingNode()) {
            throw new TaskConfigurationException("Configuração DMN ausente para a task: " + def.getNodeId());
        }

        String dmnFile = json.path("dmnFile").asText();
        String decisionKey = json.path("decisionKey").asText();
        
        if (dmnFile.isBlank() || decisionKey.isBlank()) {
            throw new TaskConfigurationException("Campos 'dmnFile' e 'decisionKey' são obrigatórios para DMN: " + def.getNodeId());
        }

        DmnDecision decision = loadDecision(dmnFile, decisionKey, def.getNodeId().value());
        
        Map<String, Object> inputs = objectMapper.convertValue(
            json.path("inputs"), 
            new TypeReference<Map<String, Object>>() {}
        );

        return new DmnTaskConfiguration(decisionKey, dmnFile, decision, inputs);
    }

    private DmnDecision loadDecision(String dmnFile, String decisionKey, String nodeId) {
        String path = "/dmn/" + dmnFile;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new TaskConfigurationException("Arquivo DMN não encontrado: " + path + " para a task: " + nodeId);
            }
            return dmnEngine.parseDecision(decisionKey, is);
        } catch (Exception e) {
            throw new TaskConfigurationException("Erro ao carregar DMN para a task: " + nodeId, e);
        }
    }
}
