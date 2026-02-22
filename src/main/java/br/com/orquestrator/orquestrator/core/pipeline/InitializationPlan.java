package br.com.orquestrator.orquestrator.core.pipeline;

import java.util.List;

/**
 * Representa o plano de inicialização para uma operação, vindo de uma configuração externa.
 */
public record InitializationPlan(
    String operationType, 
    List<InitializerDefinition> initializers
) {
    public record InitializerDefinition(String id, Integer version) {}
}