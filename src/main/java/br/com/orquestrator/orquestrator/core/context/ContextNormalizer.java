package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;
import br.com.orquestrator.orquestrator.core.context.init.ContextTaskInitializer;
import br.com.orquestrator.orquestrator.core.context.normalization.NormalizationRuleProvider;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class ContextNormalizer implements ContextTaskInitializer {

    private final NormalizationRuleProvider ruleProvider;
    private final ExpressionService expressionService;

    @Override
    public void initialize(ExecutionContext context) {
        normalize(context, context.getOperationType());
    }

    public void normalize(ExecutionContext context, String operationType) {
        List<InputNormalizationEntity> rules = ruleProvider.getRules(operationType);
        if (rules == null || rules.isEmpty()) return;

        // OTIMIZAÇÃO: Cria um único EvaluationContext para TODAS as regras da requisição
        // Evita N criações de contexto por request.
        final EvaluationContext evalContext = expressionService.create(context);

        for (InputNormalizationEntity rule : rules) {
            try {
                Object value = evalContext.evaluate(rule.getSourceExpression(), Object.class);
                if (value != null) {
                    Object transformed = transformValue(rule, value, evalContext);
                    context.put(ContextKey.STANDARD + "." + rule.getTargetField(), transformed);
                }
            } catch (Exception e) {
                log.warn("Falha ao normalizar [{}]: {}", rule.getTargetField(), e.getMessage());
            }
        }
    }

    private Object transformValue(InputNormalizationEntity rule, Object value, EvaluationContext evalContext) {
        String transformExp = rule.getTransformationExpression();
        if (transformExp == null || transformExp.isBlank()) {
            return value;
        }

        // OTIMIZAÇÃO: Injeta o 'value' no contexto principal temporariamente
        // Evita a criação de um novo EvaluationContext e Map.of() para cada transformação.
        try {
            evalContext.setVariable("value", value);
            return evalContext.evaluate(transformExp, Object.class);
        } finally {
            evalContext.setVariable("value", null); // Limpa para não vazar
        }
    }
}
