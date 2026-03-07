//package br.com.orquestrator.orquestrator.core.engine.binding;
//
//import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
//import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * DataMarshaller: Mantido apenas para compatibilidade durante a transição.
// * TODO: Remover após migrar todas as referências para os novos Compiladores.
// */
//@Component
//@RequiredArgsConstructor
//@Deprecated
//public class DataMarshaller {
//
//    private final ExpressionEngine expressionEngine;
//    private final InputCompiler inputCompiler;
//    private final OutputCompiler outputCompiler;
//    private final NormalizationCompiler normalizationCompiler;
//
//    public MarshallingPlan createPlan(TaskDefinition def) {
//        return outputCompiler.createPlan(def);
//    }
//
//    public List<NormalizationStep> createNormalizationPlan(Map<String, String> mapping) {
//        return normalizationCompiler.createPlan(mapping);
//    }
//}
