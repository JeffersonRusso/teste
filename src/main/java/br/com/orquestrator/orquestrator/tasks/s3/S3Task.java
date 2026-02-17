package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class S3Task implements Task {

    private final TaskDefinition definition;
    private final ObjectMapper objectMapper;
    private final ExpressionService expressionService;
    private final S3TaskConfiguration config;

    @Override
    public Object execute(ExecutionContext context) {
        String nodeId = definition.getNodeId().value();
        EvaluationContext evalContext = expressionService.create(context);
        
        Object content = evalContext.evaluate(config.contentExpression(), Object.class);
        
        if (content == null) {
            log.warn("Conteúdo nulo para exportação S3 na task {}. Nada será enviado.", nodeId);
            return null;
        }

        String key = evalContext.resolve(config.keyTemplate(), String.class);

        uploadToS3(config.bucket(), key, content);

        context.track(nodeId, "s3.location", STR."s3://\{config.bucket()}/\{key}");
        if (config.region() != null) {
            context.track(nodeId, "s3.region", config.region());
        }
        return content;
    }

    private void uploadToS3(String bucket, String key, Object content) {
        try {
            String jsonContent = objectMapper.writeValueAsString(content);
            log.info("[S3Task] Uploading to s3://{}/{} (Size: {} bytes)", bucket, key, jsonContent.length());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar conteúdo para S3", e);
        }
    }
}
