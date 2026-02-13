package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TagRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleRepository repository;
    private final ExpressionService expressionService;

    @Override
    public Set<String> resolveTags(ExecutionContext context) {
        Set<String> tags = new HashSet<>();
        List<TagRuleEntity> rules = getCachedRules();
        
        EvaluationContext evalContext = expressionService.create(context);

        for (TagRuleEntity rule : rules) {
            try {
                boolean match = Boolean.TRUE.equals(evalContext.evaluate(rule.getConditionExpression(), Boolean.class));
                
                if (match) {
                    tags.add(rule.getTagName());
                }
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("EL1007E")) {
                    log.debug("Regra de tag '{}' ignorada devido a propriedade nula na expressão.", rule.getTagName());
                } else {
                    log.warn("Erro ao avaliar regra de tag {}: {}", rule.getTagName(), e.getMessage());
                }
            }
        }
        return tags;
    }

    @Cacheable(value = "tag_rules", unless = "#result.isEmpty()")
    public List<TagRuleEntity> getCachedRules() {
        return repository.findByActiveTrueOrderByPriorityDesc();
    }
}
