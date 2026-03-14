package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SpelTask: Agora focada na execução limpa.
 */
@Slf4j
@RequiredArgsConstructor
public final class SpelTask implements Task {

    private final ExpressionEngine expressionEngine;
    private final CompiledConfiguration<SpelTaskConfiguration> config;
    private final DataFactory dataFactory;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        // CORREÇÃO: O config.resolve() já avaliou as expressões SpEL que estavam na configuração.
        // Se a configuração 'expression' for uma expressão SpEL, o resolve() já trouxe o resultado.
        
        // Porém, para a SpelTask, o CAMPO 'expression' contém o SCRIPT a ser executado.
        // Então primeiro resolvemos a config para pegar o script final.
        SpelTaskConfiguration resolved = config.resolve(context.getInputs());

        try {
            // Executa o script (expressão) contra o contexto de inputs
            Object result = expressionEngine.compile(resolved.expression())
                    .evaluate(context.getInputs());

            return TaskResult.success(dataFactory.createValue(result));

        } catch (Exception e) {
            log.error("Erro na execução do script SpEL [{}]: {}", context.getTaskName(), e.getMessage());
            return TaskResult.error(500, "Erro no script: " + e.getMessage());
        }
    }
}
