package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;
import br.com.orquestrator.orquestrator.core.context.init.ContextInitializer;
import br.com.orquestrator.orquestrator.core.context.normalization.NormalizationRuleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Orquestrador de normalização de contexto.
 * Refatorado para operar diretamente no ExecutionContext (JSON Vivo).
 */
@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class ContextNormalizer implements ContextInitializer {

    private final NormalizationRuleProvider ruleProvider;
    private final ExpressionService expressionService;

    @Override
    public void initialize(ExecutionContext context, String operationType) {
        normalize(context, operationType);
    }

    public void normalize(ExecutionContext context, String operationType) {
        log.debug("Iniciando normalização: {}", operationType);

        List<InputNormalizationEntity> rules = ruleProvider.getRules(operationType);
        EvaluationContext evalContext = expressionService.create(context);

        rules.forEach(rule -> applyRule(rule, evalContext, context));
        
        log.debug("Normalização concluída para {}", operationType);
    }

    private void applyRule(InputNormalizationEntity rule, EvaluationContext evalContext, ExecutionContext context) {
        try {
            extractValue(rule, evalContext)
                .map(val -> transformValue(rule, val, context))
                .ifPresent(val -> context.put(STR."\{ContextKey.STANDARD}.\{rule.getTargetField()}", val));
        } catch (Exception e) {
            log.warn("Falha ao processar regra de normalização [{}]: {}", rule.getTargetField(), e.getMessage());
        }
    }

    private Optional<Object> extractValue(InputNormalizationEntity rule, EvaluationContext evalContext) {
        return Optional.ofNullable(evalContext.evaluate(rule.getSourceExpression(), Object.class));
    }

    private Object transformValue(InputNormalizationEntity rule, Object value, ExecutionContext context) {
        String transformExp = rule.getTransformationExpression();
        
        if (transformExp == null || transformExp.isBlank()) {
            return value;
        }

        EvaluationContext transContext = expressionService.create(context, Map.of("value", value));
        return transContext.evaluate(transformExp, Object.class);
    }
}
