//package br.com.orquestrator.orquestrator.tasks.script.dmn;
//
//import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
//import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
//import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
//import br.com.orquestrator.orquestrator.core.ports.output.TaskProvider;
//import br.com.orquestrator.orquestrator.api.task.Task;
//import lombok.RequiredArgsConstructor;
//import org.camunda.bpm.dmn.engine.DmnEngine;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class DmnTaskProvider implements TaskProvider {
//
//    private final TaskBindingResolver bindingResolver;
//    private final DmnEngine dmnEngine;
//
//    @Override public String getType() { return "DMN"; }
//    @Override public Optional<Class<?>> getConfigClass() { return Optional.of(DmnTaskConfiguration.class); }
//
//    @Override
//    public Task create(TaskDefinition definition) {
//        CompiledConfiguration<DmnTaskConfiguration> config = bindingResolver.compile(
//            definition.config(),
//            DmnTaskConfiguration.class
//        );
//        return new DmnTask(dmnEngine, config);
//    }
//}
