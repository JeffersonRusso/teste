package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleRepository repository;
    private final SpelContextFactory contextFactory;

    @Override
    public Set<String> resolveTags(ExecutionContext context) {
        var activeRules = repository.findAllActive();
        
        // O contexto de avaliação agora enxerga 'raw' automaticamente via ExecutionContextAccessor
        var evalContext = contextFactory.create(context);
        
        return activeRules.stream()
                .filter(rule -> Boolean.TRUE.equals(evalContext.evaluate(rule.getConditionExpression(), Boolean.class)))
                .map(rule -> rule.getTagName())
                .collect(Collectors.toSet());
    }
}
