//package br.com.orquestrator.orquestrator.tasks.script.dmn;
//
//import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
//import br.com.orquestrator.orquestrator.tasks.base.BaseTask;
//import br.com.orquestrator.orquestrator.api.task.TaskResult;
//import com.fasterxml.jackson.databind.JsonNode;
//import lombok.RequiredArgsConstructor;
//import org.camunda.bpm.dmn.engine.DmnDecision;
//import org.camunda.bpm.dmn.engine.DmnEngine;
//import org.camunda.bpm.dmn.engine.DmnResult;
//import org.camunda.bpm.engine.variable.VariableMap;
//import org.camunda.bpm.engine.variable.Variables;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.util.Map;
//
//@RequiredArgsConstructor
//public class DmnTask extends BaseTask {
//
//    private final DmnEngine dmnEngine;
//    private final CompiledConfiguration<DmnTaskConfiguration> config;
//
//    @Override
//    protected TaskResult doExecute(Map<String, JsonNode> inputs) {
//        DmnTaskConfiguration resolved = config.resolve(inputs);
//
//        InputStream dmnStream = new ByteArrayInputStream(resolved.dmnXml().getBytes());
//        DmnDecision decision = dmnEngine.parseDecisions(dmnStream).stream()
//                .filter(d -> d.getKey().equals(resolved.decisionKey()))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Decisão DMN não encontrada: " + resolved.decisionKey()));
//
//        VariableMap variables = Variables.createVariables();
//        inputs.forEach(variables::putValue);
//
//        DmnResult result = dmnEngine.evaluateDecision(decision, variables);
//
//        // TODO: Converter DmnResult para JsonNode
//        return TaskResult.success(null);
//    }
//}
