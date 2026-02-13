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

/**
 * Especialista em extrair dados da resposta HTTP.
 * Delega o mapeamento semântico para o TaskResultMapper.
 * Java 21: Refatorado para garantir isolamento de responsabilidades.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpResponseProcessor {

    private final ObjectMapper objectMapper;
    private final StreamingJsonExtractor streamingExtractor;
    private final TaskResultMapper resultMapper;

    /**
     * Processa a resposta HTTP, extraindo o corpo e delegando o mapeamento.
     */
    public void process(ClientHttpResponse response, TaskDefinition definition, TaskData data) {
        try {
            // 1. Registro do status (Metadado de infraestrutura)
            data.addMetadata(TaskMetadataHelper.STATUS, response.getStatusCode().value());

            // 2. Extração do corpo (Streaming vs Full Tree)
            try (InputStream bodyStream = response.getBody()) {
                Object rawResult = shouldStream(definition) 
                    ? projectWithStreaming(bodyStream, definition.getProduces())
                    : parseFullBody(bodyStream);

                // 3. Delega o mapeamento semântico e o rastro de BODY para o especialista
                resultMapper.mapResult(data, rawResult, definition);
            }
        } catch (Exception e) {
            log.error("   [HttpResponseProcessor] Falha ao processar payload da task {}: {}", 
                    definition.getNodeId(), e.getMessage());
            data.addMetadata("http.error.parsing", e.getMessage());
        }
    }

    private boolean shouldStream(TaskDefinition def) {
        List<DataSpec> produces = def.getProduces();
        if (produces == null || produces.isEmpty()) return false;
        return produces.stream().anyMatch(s -> s.path() != null && !s.path().isBlank());
    }

    private Map<String, Object> projectWithStreaming(InputStream stream, List<DataSpec> produces) {
        Set<String> targetFields = produces.stream()
                .map(DataSpec::path)
                .filter(Objects::nonNull)
                .filter(p -> !p.isBlank())
                .collect(Collectors.toSet());
        return streamingExtractor.extractFields(stream, targetFields);
    }

    private Object parseFullBody(InputStream bodyStream) throws Exception {
        return objectMapper.readTree(bodyStream);
    }
}
