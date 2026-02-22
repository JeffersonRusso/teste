package br.com.orquestrator.orquestrator.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço responsável por resolver variáveis de ambiente em configurações.
 * Expurgado JsonNode em favor de Map puro.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigVariableResolver {

    private final Environment environment;
    private static final Pattern ENV_PATTERN = Pattern.compile("#\\{\\s*@environment\\.getProperty\\(['\"]([^'\"]+)['\"]\\)\\s*}");

    @SuppressWarnings("unchecked")
    public Map<String, Object> resolve(Map<String, Object> config) {
        if (config == null) return null;

        Map<String, Object> resolved = new HashMap<>();
        config.forEach((key, value) -> {
            if (value instanceof String str && str.contains("@environment")) {
                resolved.put(key, resolveEnvVars(str));
            } else if (value instanceof Map) {
                resolved.put(key, resolve((Map<String, Object>) value));
            } else {
                resolved.put(key, value);
            }
        });
        return resolved;
    }
    
    private String resolveEnvVars(String input) {
        Matcher matcher = ENV_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String propertyKey = matcher.group(1);
            String propertyValue = environment.getProperty(propertyKey, "");
            matcher.appendReplacement(sb, propertyValue);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
