package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.service.ErrorTemplateService;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
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
    protected Object interceptTyped(ExecutionContext context, TaskChain next, ResponseValidatorConfig config, TaskDefinition taskDef) {
        Object result = next.proceed(context);

        String nodeId = taskDef.getNodeId().value();
        // Correção: Acesso via constante da ExecutionContext
        Object rawStatus = context.getMeta(nodeId, ExecutionContext.STATUS);
        if (rawStatus == null || config == null || config.rules() == null || config.rules().isEmpty()) {
            return result;
        }

        EvaluationContext evalContext = createEvalContext(context, taskDef, rawStatus, result);

        for (ResponseValidatorConfig.Rule rule : config.rules()) {
            if (isConditionMet(rule, evalContext)) {
                handleValidationFailure(taskDef, rule, evalContext);
            }
        }

        context.track(nodeId, "validation.passed", true);
        return result;
    }

    private EvaluationContext createEvalContext(ExecutionContext context, TaskDefinition taskDef, Object rawStatus, Object result) {
        Map<String, Object> vars = Map.of(
            "node_id", taskDef.getNodeId().value(),
            "node_status", conversionService.convert(rawStatus, Integer.class),
            "node_body", result != null ? result : Map.of()
        );
        return expressionService.create(context, vars);
    }

    private boolean isConditionMet(ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        return rule.condition() != null && 
               Boolean.TRUE.equals(evalContext.evaluate(rule.condition(), Boolean.class));
    }

    private void handleValidationFailure(TaskDefinition taskDef, ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        String messageTemplate = rule.message() != null ? rule.message() : 
                                 (rule.errorCode() != null ? errorTemplateService.getTemplate(rule.errorCode()) : "Validation Error");
        
        String resolvedMessage = evalContext.resolve(messageTemplate, String.class);

        PipelineException ex = new PipelineException(resolvedMessage)
                .withNodeId(taskDef.getNodeId().value())
                .addMetadata("interceptor", "RESPONSE_VALIDATOR")
                .addMetadata("failed_condition", rule.condition());

        if (rule.metadata() != null && rule.metadata().isObject()) {
            rule.metadata().properties().forEach(entry ->
                ex.addMetadata(entry.getKey(), entry.getValue().asText())
            );
        }

        log.error("   [ResponseValidator] Validation failure on {}: {}", taskDef.getNodeId(), resolvedMessage);
        throw ex;
    }
}
