package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class GroovyTask implements Task {

    private final CompiledConfiguration<GroovyTaskConfiguration> config;
    private final DataFactory dataFactory;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        GroovyTaskConfiguration resolved = config.resolve(context.getInputs());

        try {
            // CORREÇÃO: Chamada sem parâmetros ao getNativeInputs()
            Binding binding = new Binding(context.getNativeInputs());
            GroovyShell shell = new GroovyShell(binding);

            Object result = shell.evaluate(resolved.script());

            return TaskResult.success(dataFactory.createValue(result));

        } catch (Exception e) {
            log.error("Erro no script Groovy [{}]: {}", context.getTaskName(), e.getMessage());
            return TaskResult.error(500, "Falha no script Groovy: " + e.getMessage());
        }
    }
}
