package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Executor S3: Único lugar que conhece a SDK da AWS e serialização para upload.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Executor {

    private final ObjectMapper objectMapper;

    public TaskResult upload(String bucket, String key, String region, Object content) {
        try {
            String jsonContent = objectMapper.writeValueAsString(content);
            log.info("[S3Executor] Uploading to s3://{}/{} (Size: {} bytes)", bucket, key, jsonContent.length());
            
            // Simulação de upload (Aqui entraria a SDK da AWS)
            
            return TaskResult.success(content, Map.of(
                "s3.location", STR."s3://\{bucket}/\{key}",
                "s3.region", region != null ? region : "default"
            ));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar upload para S3", e);
        }
    }
}
