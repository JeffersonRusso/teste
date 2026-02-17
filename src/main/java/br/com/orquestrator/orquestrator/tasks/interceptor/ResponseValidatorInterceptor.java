package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.service.ErrorTemplateService;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("RESPONSE_VALIDATOR")
public class ResponseValidatorInterceptor extends BaseValidationInterceptor<ResponseValidatorConfig> {

    private final ErrorTemplateService errorTemplateService;
    private final ExpressionService expressionService;

    public ResponseValidatorInterceptor(ErrorTemplateService errorTemplateService,
                                        ExpressionService expressionService) {
        super(ResponseValidatorConfig.class);
        this.errorTemplateService = errorTemplateService;
        this.expressionService = expressionService;
    }

    @Override
    protected void validate(TaskResult result, ResponseValidatorConfig config, ExecutionContext context, TaskDefinition taskDef) {
        if (config == null || config.rules() == null || config.rules().isEmpty()) return;

        EvaluationContext evalContext = createEvalContext(context, taskDef, result);

        for (ResponseValidatorConfig.Rule rule : config.rules()) {
            if (Boolean.TRUE.equals(evalContext.evaluate(rule.condition(), Boolean.class))) {
                handleFailure(taskDef, rule, evalContext);
            }
        }
        context.track(taskDef.getNodeId().value(), "validation.passed", true);
    }

    private EvaluationContext createEvalContext(ExecutionContext context, TaskDefinition taskDef, TaskResult result) {
        Map<String, Object> vars = Map.of(
            "node_id", taskDef.getNodeId().value(),
            "node_status", result.status(),
            "node_body", result.body() != null ? result.body() : Map.of()
        );
        return expressionService.create(context, vars);
    }

    private void handleFailure(TaskDefinition taskDef, ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        String messageTemplate = rule.message() != null ? rule.message() : 
                                 (rule.errorCode() != null ? errorTemplateService.getTemplate(rule.errorCode()) : "Validation Error");
        
        String resolvedMessage = evalContext.resolve(messageTemplate, String.class);
        throw new PipelineException(resolvedMessage).withNodeId(taskDef.getNodeId().value());
    }
}
