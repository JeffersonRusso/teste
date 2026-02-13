package br.com.orquestrator.orquestrator.tasks.tracing.extractor;

import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.tracing.MetadataExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class HttpMetadataExtractor implements MetadataExtractor {

    @Override
    public boolean supports(String taskType) {
        return "HTTP".equalsIgnoreCase(taskType);
    }

    @Override
    public void extract(TaskDefinition def, ExecutionSpan span) {
        JsonNode config = def.getConfig();
        if (config == null) return;

        if (config.has("url")) span.addMetadata("url", config.get("url").asText());
        if (config.has("method")) span.addMetadata("method", config.get("method").asText());
    }
}
