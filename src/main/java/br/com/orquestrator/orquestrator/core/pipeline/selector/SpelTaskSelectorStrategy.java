package br.com.orquestrator.orquestrator.core.pipeline.selector;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpelTaskSelectorStrategy implements TaskSelectorStrategy {

    private final ExpressionService expressionService;

    @Override
    public boolean shouldRun(TaskDefinition task, ExecutionContext context) {
        String selector = task.getSelectorExpression();
        
        if (!hasText(selector)) {
            return true;
        }

        try {
            EvaluationContext evalContext = expressionService.create(context);
            return Boolean.TRUE.equals(evalContext.evaluate(selector, Boolean.class));
        } catch (Exception e) {
            log.error("Erro ao avaliar seletor da task {}: {}", task.getNodeId(), e.getMessage());
            return false;
        }
    }
}
