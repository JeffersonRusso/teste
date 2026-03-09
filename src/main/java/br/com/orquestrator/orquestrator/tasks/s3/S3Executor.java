package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class S3Executor {
    public TaskResult upload(String bucket, String key, String region, String content) {
        // Lógica de upload para o S3
        return TaskResult.success(JsonNodeFactory.instance.pojoNode(content), Map.of(
            "bucket", bucket,
            "key", key
        ));
    }
}
