package br.com.orquestrator.orquestrator.tasks.tracing.extractor;

import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.tracing.MetadataExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class SpelMetadataExtractor implements MetadataExtractor {

    @Override
    public boolean supports(String taskType) {
        return "SPEL".equalsIgnoreCase(taskType);
    }

    @Override
    public void extract(TaskDefinition def, ExecutionSpan span) {
        JsonNode config = def.getConfig();
        if (config == null) return;

        if (config.has("expression")) span.addMetadata("expression", config.get("expression").asText());
    }
}
