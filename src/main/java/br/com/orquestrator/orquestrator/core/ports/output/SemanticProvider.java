package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.rules.SemanticDefinition;

import java.util.List;

/**
 * SemanticProvider: Porta de saída para buscar as definições semânticas.
 */
public interface SemanticProvider {
    
    /**
     * Retorna todas as definições semânticas.
     */
    List<SemanticDefinition> findAll();
}
