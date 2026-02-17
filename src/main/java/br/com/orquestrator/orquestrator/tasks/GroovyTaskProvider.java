package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.script.groovy.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroovyTaskProvider implements TaskProvider {

    private final GroovyScriptLoader scriptLoader;
    private final GroovyBindingFactory bindingFactory;

    @Override
    public String getType() {
        return "GROOVY_SCRIPT";
    }

    @Override
    public Task create(TaskDefinition def) {
        GroovyTaskConfiguration config = parseConfig(def.getConfig());
        return new GroovyTask(def, scriptLoader, bindingFactory, config);
    }

    private GroovyTaskConfiguration parseConfig(JsonNode json) {
        if (json == null || json.isMissingNode()) {
            throw new TaskConfigurationException("Configuração Groovy ausente");
        }

        String scriptName = json.path("scriptName").asText(null);
        String scriptBody = json.path("scriptBody").asText(null);
        
        Map<String, Object> additionalParams = new HashMap<>();
        if (json.has("params") && json.get("params").isObject()) {
            json.get("params").properties().forEach(entry ->
                additionalParams.put(entry.getKey(), entry.getValue().asText())
            );
        }

        return new GroovyTaskConfiguration(scriptName, scriptBody, additionalParams);
    }
}
