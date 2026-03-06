package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AviatorTaskProvider implements TaskProvider {
    @Override public String getType() { return "AVIATOR"; }
    @Override public Optional<Class<?>> getConfigClass() { return Optional.of(ScriptTaskConfiguration.class); }
    @Override public Task create(TaskDefinition definition) { return new AviatorTask(); }
}
