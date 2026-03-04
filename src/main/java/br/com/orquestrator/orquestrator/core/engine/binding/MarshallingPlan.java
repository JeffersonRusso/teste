package br.com.orquestrator.orquestrator.core.engine.binding;

import java.util.Map;

/**
 * MarshallingPlan: Um plano de execução imutável para movimentação de dados.
 * Criado no build-time para performance máxima no run-time.
 */
public record MarshallingPlan(
    Map<String, String> inputMap,  // LocalKey -> GlobalKey
    Map<String, String> outputMap, // LocalKey -> GlobalKey
    String nodeId
) {}
