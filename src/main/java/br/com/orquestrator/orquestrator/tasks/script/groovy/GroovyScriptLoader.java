package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.cache.GroovyScriptCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Script;
import org.springframework.stereotype.Component;

@Component
public class GroovyScriptLoader {

    private final GroovyScriptCache scriptCache;
    private final ObjectMapper objectMapper;

    public GroovyScriptLoader(GroovyScriptCache scriptCache, ObjectMapper objectMapper) {
        this.scriptCache = scriptCache;
        this.objectMapper = objectMapper;
    }

    public void validateConfig(JsonNode config, NodeId nodeId) {
        try {
            GroovyTaskConfiguration cfg = objectMapper.treeToValue(config, GroovyTaskConfiguration.class);
            if (cfg.scriptBody() == null && cfg.scriptName() == null) {
                throw new TaskConfigurationException("Configuração da task Groovy deve ter 'scriptBody' ou 'scriptName': " + nodeId.value());
            }
        } catch (JsonProcessingException e) {
            throw new TaskConfigurationException("Erro ao parsear configuração da task Groovy: " + nodeId.value(), e);
        }
    }

    public Class<? extends Script> load(JsonNode config, NodeId nodeId) {
        try {
            GroovyTaskConfiguration cfg = objectMapper.treeToValue(config, GroovyTaskConfiguration.class);
            
            if (cfg.scriptBody() != null) {
                return scriptCache.getOrCompile("inline:" + nodeId.value(), cfg.scriptBody());
            }

            if (cfg.scriptName() != null) {
                return scriptCache.getOrCompileFile(cfg.scriptName());
            }
            
            throw new TaskConfigurationException("Configuração inválida para task Groovy: " + nodeId.value());
        } catch (JsonProcessingException e) {
            throw new TaskConfigurationException("Erro ao carregar script Groovy: " + nodeId.value(), e);
        }
    }
}
