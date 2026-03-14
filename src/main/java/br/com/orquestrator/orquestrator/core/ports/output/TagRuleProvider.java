package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.rules.TagRule;

import java.util.List;

/**
 * TagRuleProvider: Porta de saída para prover regras de tags.
 * O Domínio solicita as regras por meio dessa interface.
 */
public interface TagRuleProvider {
    
    /**
     * Retorna todas as regras ativas ordenadas por prioridade decrescente.
     */
    List<TagRule> findAllActive();
}
