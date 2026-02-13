package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
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
    private final TaskResultMapper resultMapper;

    @Override
    public String getType() {
        return "GROOVY_SCRIPT";
    }

    @Override
    public Task create(TaskDefinition def) {
        // O Provider extrai a configuração do JSON aqui
        GroovyTaskConfiguration config = parseConfig(def.getConfig());
        
        // Instancia a Task passando a configuração já tipada
        return new GroovyTask(def, scriptLoader, bindingFactory, resultMapper, config);
    }

    private GroovyTaskConfiguration parseConfig(JsonNode json) {
        if (json == null || json.isMissingNode()) {
            throw new TaskConfigurationException("Configuração Groovy ausente");
        }

        String scriptName = json.path("scriptName").asText(null);
        String scriptBody = json.path("scriptBody").asText(null);
        
        Map<String, Object> additionalParams = new HashMap<>();
        // Exemplo: extrair parâmetros extras se houver um nó "params"
        if (json.has("params") && json.get("params").isObject()) {
            json.get("params").fields().forEachRemaining(entry -> 
                additionalParams.put(entry.getKey(), entry.getValue().asText())
            );
        }

        return new GroovyTaskConfiguration(scriptName, scriptBody, additionalParams);
    }
}
