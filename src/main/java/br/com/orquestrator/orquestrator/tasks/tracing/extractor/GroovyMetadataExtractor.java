package br.com.orquestrator.orquestrator.tasks.tracing.extractor;

import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.tracing.MetadataExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class GroovyMetadataExtractor implements MetadataExtractor {

    @Override
    public boolean supports(String taskType) {
        return "GROOVY_SCRIPT".equalsIgnoreCase(taskType);
    }

    @Override
    public void extract(TaskDefinition def, ExecutionSpan span) {
        JsonNode config = def.getConfig();
        if (config == null) return;

        if (config.has("scriptBody")) span.addMetadata("script", config.get("scriptBody").asText());
        if (config.has("scriptName")) span.addMetadata("scriptName", config.get("scriptName").asText());
    }
}
