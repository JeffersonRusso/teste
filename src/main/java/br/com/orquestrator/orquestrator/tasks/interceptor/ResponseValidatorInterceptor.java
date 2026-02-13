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
 * Utiliza expressões SpEL para inspecionar status e corpo da resposta.
 * Java 21: Refatorado para maior clareza, imutabilidade e rastro rico de erros.
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
        // 1. Executa a task primeiro (ou o próximo interceptor)
        next.proceed(data);

        // 2. Fail Fast: Sem regras ou sem status, não há o que validar
        Object rawStatus = data.getMetadata(TaskMetadataHelper.STATUS);
        if (rawStatus == null || config == null || config.rules() == null || config.rules().isEmpty()) {
            return;
        }

        // 3. Preparação do Contexto de Expressão (Unificado)
        EvaluationContext evalContext = createEvalContext(data, taskDef, rawStatus);

        // 4. Validação das Regras
        for (ResponseValidatorConfig.Rule rule : config.rules()) {
            if (isConditionMet(rule, evalContext)) {
                handleValidationFailure(data, taskDef, rule, evalContext);
            }
        }

        data.addMetadata("validation.passed", true);
    }

    private EvaluationContext createEvalContext(TaskData data, TaskDefinition taskDef, Object rawStatus) {
        // Java 21: Map.of para imutabilidade e clareza
        Map<String, Object> vars = Map.of(
            "node_id", taskDef.getNodeId().value(),
            "node_status", conversionService.convert(rawStatus, Integer.class),
            "node_body", data.getMetadata(TaskMetadataHelper.BODY) != null ? data.getMetadata(TaskMetadataHelper.BODY) : Map.of()
        );
        return expressionService.create(data, vars);
    }

    private boolean isConditionMet(ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        return rule.condition() != null && 
               Boolean.TRUE.equals(evalContext.evaluate(rule.condition(), Boolean.class));
    }

    private void handleValidationFailure(TaskData data, TaskDefinition taskDef, ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        String messageTemplate = rule.message() != null ? rule.message() : 
                                 (rule.errorCode() != null ? errorTemplateService.getTemplate(rule.errorCode()) : "Validation Error");
        
        String resolvedMessage = evalContext.resolve(messageTemplate, String.class);

        // Java 21: Construção de exceção rica com metadados e String Templates
        PipelineException ex = new PipelineException(resolvedMessage)
                .withNodeId(taskDef.getNodeId().value())
                .addMetadata("interceptor", "RESPONSE_VALIDATOR")
                .addMetadata("failed_condition", rule.condition());

        // Extração limpa de metadados customizados da regra
        if (rule.metadata() != null && rule.metadata().isObject()) {
            rule.metadata().fields().forEachRemaining(entry -> 
                ex.addMetadata(entry.getKey(), entry.getValue().asText())
            );
        }

        log.error("   [ResponseValidator] Validation failure on {}: {}", taskDef.getNodeId(), resolvedMessage);
        throw ex;
    }
}
