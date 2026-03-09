//package br.com.orquestrator.orquestrator.core.engine.binding;
//
//import br.com.orquestrator.orquestrator.domain.vo.DataPath;
//import java.util.Map;
//
///**
// * MarshallingPlan: Plano de execução imutável e pré-compilado.
// */
//public record MarshallingPlan(
//    Map<String, String> inputMap,
//    Map<OutputMapper, DataPath> outputPlan, // Estratégia -> Destino
//    Map<String, String> rawOutputMap,       // Expressão Original -> Destino (Para otimizações)
//    String nodeId
//) {}
