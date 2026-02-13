package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3Task extends AbstractTask {

    private final S3TaskConfiguration config;
    private final ExpressionService expressionService;
    private final ObjectMapper objectMapper;

    public S3Task(TaskDefinition definition, 
                  ObjectMapper objectMapper,
                  ExpressionService expressionService,
                  S3TaskConfiguration config) {
        super(definition);
        this.objectMapper = objectMapper;
        this.expressionService = expressionService;
        this.config = config;
    }

    @Override
    public void validateConfig() {
        if (config.bucket() == null || config.bucket().isBlank()) {
            throw new TaskConfigurationException("Bucket é obrigatório para S3Task: " + definition.getNodeId());
        }
    }

    @Override
    public void execute(TaskData data) {
        EvaluationContext evalContext = expressionService.create(data);
        
        Object content = evalContext.evaluate(config.contentExpression(), Object.class);
        
        if (content == null) {
            log.warn("Conteúdo nulo para exportação S3 na task {}. Nada será enviado.", definition.getNodeId());
            return;
        }

        String key = evalContext.resolve(config.keyTemplate(), String.class);

        uploadToS3(config.bucket(), key, content);

        data.addMetadata("s3.location", STR."s3://\{config.bucket()}/\{key}");
        if (config.region() != null) {
            data.addMetadata("s3.region", config.region());
        }
    }

    private void uploadToS3(String bucket, String key, Object content) {
        try {
            String jsonContent = objectMapper.writeValueAsString(content);
            log.info("[S3Task] Uploading to s3://{}/{} (Size: {} bytes)", bucket, key, jsonContent.length());
            // Aqui entraria a chamada real ao SDK da AWS
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar conteúdo para S3", e);
        }
    }
}
