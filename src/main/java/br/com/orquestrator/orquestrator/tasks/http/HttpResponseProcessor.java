package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.json.StreamingJsonExtractor;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpResponseProcessor {

    private final ObjectMapper objectMapper;
    private final TaskResultMapper resultMapper;
    private final StreamingJsonExtractor streamingExtractor;

    public void process(ClientHttpResponse response, TaskDefinition definition, TaskData data) {
        Object result = null;
        
        try {
            int statusCode = response.getStatusCode().value();
            data.addMetadata(TaskMetadataHelper.STATUS, statusCode);

            try (InputStream bodyStream = response.getBody()) {
                if (hasProjection(definition)) {
                    result = projectWithStreaming(bodyStream, definition.getProduces());
                } else {
                    result = parseFullBody(bodyStream);
                }
            }
        } catch (Exception e) {
            log.warn("   [HttpTask] Erro ao processar resposta: {}", e.getMessage());
        }

        // O resultMapper agora precisa ser adaptado para TaskData
        resultMapper.mapResult(data, result, definition);
    }

    private boolean hasProjection(TaskDefinition def) {
        if (def.getProduces() == null || def.getProduces().isEmpty()) return false;
        return def.getProduces().stream().allMatch(s -> s.path() != null && !s.path().isBlank());
    }

    private Map<String, Object> projectWithStreaming(InputStream stream, List<DataSpec> produces) {
        Set<String> targetFields = produces.stream()
                .map(DataSpec::path)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
                
        return streamingExtractor.extractFields(stream, targetFields);
    }

    private Object parseFullBody(InputStream bodyStream) throws Exception {
        return objectMapper.readTree(bodyStream);
    }
}
