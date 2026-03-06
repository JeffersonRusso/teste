package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RuleBasedTagProvider: Decide cenários baseando-se em regras pré-compiladas.
 * Otimizado para 100k TPS: Zero queries e zero parses no caminho quente.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleRepository repository;
    private final ExpressionEngine expressionEngine;
    
    // Cache de regras pré-compiladas
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
                    expressionEngine.parse(rule.getConditionExpression())
                ))
                .toList();
    }

    @Override
    public Set<String> resolve(ReadableContext context) {
        // CAMINHO QUENTE: Apenas iteração e execução de expressões já compiladas
        return compiledRules.stream()
                .filter(rule -> Boolean.TRUE.equals(expressionEngine.execute(rule.expression(), context).raw()))
                .map(CompiledTagRule::tagName)
                .collect(Collectors.toSet());
    }

    private record CompiledTagRule(String tagName, Expression expression) {}
}
