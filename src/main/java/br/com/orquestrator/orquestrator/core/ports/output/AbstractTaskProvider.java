package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * AbstractTaskProvider: Padroniza a criação de Tasks com configuração compilada.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTaskProvider<C> implements TaskProvider {

    private final TaskBindingResolver bindingResolver;
    private final Class<C> configClass;

    @Override
    public final Optional<Class<?>> getConfigClass() {
        return Optional.of(configClass);
    }

    @Override
    public final Task create(TaskDefinition definition) {
        CompiledConfiguration<C> compiledConfig = bindingResolver.compile(definition.config(), configClass);
        return createTask(definition, compiledConfig);
    }

    protected abstract Task createTask(TaskDefinition definition, CompiledConfiguration<C> config);
}
