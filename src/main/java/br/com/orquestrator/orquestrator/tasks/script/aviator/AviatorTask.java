package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class AviatorTask implements Task {

    private final CompiledConfiguration<AviatorTaskConfiguration> config;
    private final DataFactory dataFactory;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        AviatorTaskConfiguration resolved = config.resolve(context.getInputs());

        try {
            Expression expression = AviatorEvaluator.compile(resolved.script(), true);
            
            // CORREÇÃO: Chamada sem parâmetros ao getNativeInputs()
            Object result = expression.execute(context.getNativeInputs());

            return TaskResult.success(dataFactory.createValue(result));

        } catch (Exception e) {
            log.error("Erro no script Aviator [{}]: {}", context.getTaskName(), e.getMessage());
            return TaskResult.error(500, "Falha no script Aviator: " + e.getMessage());
        }
    }
}
