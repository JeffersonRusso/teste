package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.service.ErrorTemplateService;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Validador de resposta que aplica regras de negócio sobre o resultado da task.
 */
@Slf4j
@Component("RESPONSE_VALIDATOR")
public class ResponseValidatorInterceptor extends TypedTaskInterceptor<ResponseValidatorConfig> {

    private final ErrorTemplateService errorTemplateService;
    private final ExpressionService expressionService;
    private final ConversionService conversionService;

    public ResponseValidatorInterceptor(ErrorTemplateService errorTemplateService,
                                        ExpressionService expressionService,
                                        ConversionService conversionService) {
        super(ResponseValidatorConfig.class);
        this.errorTemplateService = errorTemplateService;
        this.expressionService = expressionService;
        this.conversionService = conversionService;
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, ResponseValidatorConfig config, TaskDefinition taskDef) {
        // 1. Executa a task (ou o próximo interceptor)
        next.proceed(data);

        // 2. Se a task falhou na infraestrutura (exceção lançada), este código não será alcançado.
        // Se chegou aqui, a task "completou". Verificamos se ela produziu um status.
        Object rawStatus = data.getMetadata(TaskMetadataHelper.STATUS);
        if (rawStatus == null) {
            log.trace("   [ResponseValidator] Ignorando validação: Nenhum status code encontrado para {}", taskDef.getNodeId());
            return;
        }

        if (config == null || config.rules() == null || config.rules().isEmpty()) {
            return;
        }

        Integer status = conversionService.convert(rawStatus, Integer.class);
        Object body = data.getMetadata(TaskMetadataHelper.BODY);

        Map<String, Object> vars = Map.of(
            "node_id", taskDef.getNodeId().value(),
            "node_status", status != null ? status : 0,
            "node_body", body != null ? body : Map.of()
        );
        
        EvaluationContext evalContext = expressionService.create(data, vars);

        for (ResponseValidatorConfig.Rule rule : config.rules()) {
            if (rule.condition() != null && Boolean.TRUE.equals(evalContext.evaluate(rule.condition(), Boolean.class))) {
                data.addMetadata("validation.failed_condition", rule.condition());
                handleValidationFailure(data, taskDef, rule, evalContext);
            }
        }

        data.addMetadata("validation.passed", true);
    }

    private void handleValidationFailure(TaskData data, TaskDefinition taskDef, ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        String messageTemplate = getMessageTemplate(rule);
        String resolvedMessage = evalContext.resolve(messageTemplate, String.class);

        PipelineException ex = new PipelineException(resolvedMessage)
                .withNodeId(taskDef.getNodeId().value())
                .addMetadata("interceptor", "RESPONSE_VALIDATOR")
                .addMetadata("condition", rule.condition());

        if (rule.metadata() != null && rule.metadata().isObject()) {
            rule.metadata().fieldNames().forEachRemaining(fieldName -> 
                ex.addMetadata(fieldName, rule.metadata().get(fieldName).asText())
            );
        }

        log.error("   [ResponseValidator] Falha na validação da task {}: {}", taskDef.getNodeId(), resolvedMessage);
        throw ex;
    }

    private String getMessageTemplate(ResponseValidatorConfig.Rule rule) {
        if (rule.message() != null) return rule.message();
        return rule.errorCode() != null ? errorTemplateService.getTemplate(rule.errorCode()) : "Erro de validação";
    }
}
