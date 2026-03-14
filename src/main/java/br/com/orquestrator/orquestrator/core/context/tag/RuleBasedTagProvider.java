package br.com.orquestrator.orquestrator.core.context.tag;

import br.com.orquestrator.orquestrator.core.ports.output.TagRuleProvider;
import br.com.orquestrator.orquestrator.domain.rules.TagRule;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RuleBasedTagProvider: Motor de resolução de tags baseado em regras dinâmicas.
 * 
 * Implementação puramente baseada em Domínio e Abstrações de Infraestrutura.
 * Desacoplado de JPA/Entidades.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleBasedTagProvider implements TagProvider {

    private final TagRuleProvider ruleProvider;
    private final ExpressionEngine expressionEngine;

    /**
     * Resolve as tags ativas com base nas regras cadastradas e no contexto da requisição.
     * 
     * @param headers Cabeçalhos da requisição
     * @param body Corpo da requisição
     * @return Conjunto de tags ativas
     */
    @Override
    public Set<String> resolve(Map<String, String> headers, Map<String, Object> body) {
        log.debug("Iniciando resolução dinâmica de tags...");

        // Cria o contexto de avaliação unificado (Headers + Body)
        Map<String, Object> context = createContext(headers, body);

        // Busca as regras do Provider (que pode ser Banco, Cache, etc.)
        // E filtra as tags cujas condições sejam verdadeiras
        return ruleProvider.findAllActive().stream()
                .filter(rule -> evaluate(rule, context))
                .map(TagRule::tagName)
                .collect(Collectors.toSet());
    }

    private Map<String, Object> createContext(Map<String, String> headers, Map<String, Object> body) {
        Map<String, Object> context = new HashMap<>();
        if (headers != null) context.put("headers", headers);
        if (body != null) context.putAll(body);
        return context;
    }

    private boolean evaluate(TagRule rule, Map<String, Object> context) {
        try {
            return Boolean.TRUE.equals(
                expressionEngine.compile(rule.conditionExpression())
                                .evaluate(context, Boolean.class)
            );
        } catch (Exception e) {
            log.error("Erro ao avaliar regra de tag '{}': {}", rule.tagName(), e.getMessage());
            return false;
        }
    }
}
