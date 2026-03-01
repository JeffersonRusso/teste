package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.json;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskConfig: Mapeia o JSON de configuração da task.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskConfig {

    private Long timeoutMs;
    private Boolean global = false;
    private String cron;

    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Map<String, Object> toFullMap() {
        Map<String, Object> full = new HashMap<>(properties);
        if (timeoutMs != null) full.put("timeoutMs", timeoutMs);
        if (global != null) full.put("global", global);
        if (cron != null) full.put("cron", cron);
        return full;
    }
}
