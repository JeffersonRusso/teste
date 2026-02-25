//package br.com.orquestrator.orquestrator.adapter.persistence.repository.record;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import java.util.List;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//public record FeaturePhasesEntityRecord(
//    List<FeatureDefinitionEntityRecord> monitors,
//    List<FeatureDefinitionEntityRecord> preExecution,
//    List<FeatureDefinitionEntityRecord> postExecution
//) {
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public record FeatureDefinitionEntityRecord(String type, String templateRef, java.util.Map<String, Object> config) {}
//}