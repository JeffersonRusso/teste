package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.service.ErrorTemplateService;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.BaseValidationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("RESPONSE_VALIDATOR_INTERCEPTOR")
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
    protected void validate(TaskResult result, ResponseValidatorConfig config, ExecutionContext context) {
        if (config == null || config.rules() == null || config.rules().isEmpty()) return;

        String nodeId = context.getOperationType(); // Ou capturar do ScopedValue se necessário
        EvaluationContext evalContext = createEvalContext(context, nodeId, result);

        for (ResponseValidatorConfig.Rule rule : config.rules()) {
            if (Boolean.TRUE.equals(evalContext.evaluate(rule.condition(), Boolean.class))) {
                handleFailure(nodeId, rule, evalContext);
            }
        }
        context.track(nodeId, "validation.passed", true);
    }

    private EvaluationContext createEvalContext(ExecutionContext context, String nodeId, TaskResult result) {
        Map<String, Object> vars = Map.of(
            "node_id", nodeId,
            "node_status", result.status(),
            "node_body", result.body() != null ? result.body() : Map.of()
        );
        return expressionService.create(context, vars);
    }

    private void handleFailure(String nodeId, ResponseValidatorConfig.Rule rule, EvaluationContext evalContext) {
        String messageTemplate = rule.message() != null ? rule.message() : 
                                 (rule.errorCode() != null ? errorTemplateService.getTemplate(rule.errorCode()) : "Validation Error");
        
        String resolvedMessage = evalContext.resolve(messageTemplate, String.class);
        throw new PipelineException(resolvedMessage).withNodeId(nodeId);
    }
}
