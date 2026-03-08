package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TagRuleRepository;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * RuleBasedTagProvider: Resolve tags baseadas em regras dinâmicas (SpEL).
 * Agora desacoplado do ExecutionContext e focado no contexto da requisição.
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
        log.info("Compilando regras de tags dinâmicas...");
        this.compiledRules = repository.findAllActive().stream()
                .map(rule -> new CompiledTagRule(
                    rule.getTagName(),
                    // Avalia a expressão contra o mapa de inputs (headers + body)
                    (ctx) -> Boolean.TRUE.equals(expressionEngine.compile(rule.getConditionExpression()).evaluate(ctx, Boolean.class))
                ))
                .toList();
    }

    @Override
    public Set<String> resolve(Map<String, String> headers, Map<String, Object> body) {
        // Monta um contexto temporário para a avaliação das regras
        Map<String, Object> evaluationContext = new java.util.HashMap<>();
        if (headers != null) evaluationContext.put("headers", headers);
        if (body != null) evaluationContext.putAll(body);

        return compiledRules.stream()
                .filter(rule -> rule.condition().test(evaluationContext))
                .map(CompiledTagRule::tagName)
                .collect(Collectors.toSet());
    }

    private record CompiledTagRule(String tagName, Predicate<Map<String, Object>> condition) {}
}
