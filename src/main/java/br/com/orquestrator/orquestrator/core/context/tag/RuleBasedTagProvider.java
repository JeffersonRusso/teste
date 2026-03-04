package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * RuleBasedTagProvider: Decide cenários baseando-se apenas em LEITURA.
 * Utiliza o motor de expressões unificado.
 */
@Component
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleRepository repository;
    private final ExpressionEngine expressionEngine; // <--- Injeta a abstração

    @Override
    public Set<String> resolveTags(ReadableContext context) {
        var activeRules = repository.findAllActive();
        
        return activeRules.stream()
                .filter(rule -> {
                    // Avalia a regra contra o contexto de leitura
                    return Boolean.TRUE.equals(expressionEngine.evaluate(
                        rule.getConditionExpression(), 
                        context, 
                        Boolean.class
                    ));
                })
                .map(rule -> rule.getTagName())
                .collect(Collectors.toSet());
    }
}
