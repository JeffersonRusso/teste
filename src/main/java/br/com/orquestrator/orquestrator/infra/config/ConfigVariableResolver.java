package br.com.orquestrator.orquestrator.infra.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço responsável por resolver variáveis de ambiente em configurações JSON.
 * Suporta o padrão #{ @environment.getProperty('chave') }.
 * Java 21: Utiliza String Templates e lógica recursiva limpa.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigVariableResolver {

    private final Environment environment;
    
    // Regex para capturar #{ @environment.getProperty('chave') }
    private static final Pattern ENV_PATTERN = Pattern.compile("#\\{\\s*@environment\\.getProperty\\(['\"]([^'\"]+)['\"]\\)\\s*}");

    /**
     * Varre o nó JSON recursivamente e substitui placeholders por valores do ambiente.
     */
    public JsonNode resolve(JsonNode config) {
        if (config == null || !config.isObject()) return config;

        ObjectNode newConfig = config.deepCopy();
        Iterator<Map.Entry<String, JsonNode>> fields = newConfig.fields();
        
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode valueNode = field.getValue();
            
            if (valueNode.isTextual()) {
                String value = valueNode.asText();
                if (value.contains("@environment")) {
                    newConfig.set(field.getKey(), new TextNode(resolveEnvVars(value)));
                }
            } else if (valueNode.isObject()) {
                newConfig.set(field.getKey(), resolve(valueNode));
            }
        }
        return newConfig;
    }
    
    private String resolveEnvVars(String input) {
        Matcher matcher = ENV_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        
        while (matcher.find()) {
            String propertyKey = matcher.group(1);
            String propertyValue = environment.getProperty(propertyKey);
            
            if (propertyValue == null) {
                log.warn(STR."Propriedade de ambiente não encontrada: \{propertyKey}");
                propertyValue = ""; 
            }
            
            matcher.appendReplacement(sb, propertyValue);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
