package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * RuleBasedTagProvider: Puramente funcional.
 * Decide cenários baseando-se em predicados pré-compilados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleRepository repository;
    private final ExpressionEngine expressionEngine;
    
    private List<CompiledTagRule> compiledRules;

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        log.info("Compilando regras de tags para o caminho quente...");
        this.compiledRules = repository.findAllActive().stream()
                .map(rule -> new CompiledTagRule(
                    rule.getTagName(),
                    // A PONTE: Transforma CompiledExpression em Predicate puro
                    (ctx) -> Boolean.TRUE.equals(expressionEngine.compile(rule.getConditionExpression()).evaluate(ctx, Boolean.class))
                ))
                .toList();
    }

    @Override
    public Set<String> resolve(ReadableContext context) {
        return compiledRules.stream()
                .filter(rule -> rule.condition().test(context))
                .map(CompiledTagRule::tagName)
                .collect(Collectors.toSet());
    }

    private record CompiledTagRule(String tagName, Predicate<ReadableContext> condition) {}
}
