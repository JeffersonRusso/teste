package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TagRuleEntity;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleRepository repository;
    private final ExpressionService expressionService;

    @Override
    public Set<String> resolveTags(ExecutionContext context) {
        // O repositório já gerencia o cache das regras ativas
        List<TagRuleEntity> rules = repository.findByActiveTrueOrderByPriorityDesc();
        EvaluationContext evalContext = expressionService.create(context);

        return rules.stream()
                .filter(rule -> Boolean.TRUE.equals(evalContext.evaluate(rule.getConditionExpression(), Boolean.class)))
                .map(TagRuleEntity::getTagName)
                .collect(Collectors.toSet());
    }
}
