/*
package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

// CLASSE DESCONTINUADA: Carregamento de pipelines agora é feito via PipelineRepository (Banco de Dados).
@Component
@RequiredArgsConstructor
public class LegacyYamlPipelineLoader {

    private final ObjectMapper yamlMapper;

    public JsonNode load(Resource resource) {
        try {
            return yamlMapper.readTree(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar pipeline legado", e);
        }
    }
}
*/
