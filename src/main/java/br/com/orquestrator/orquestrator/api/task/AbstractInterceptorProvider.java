package br.com.orquestrator.orquestrator.api.task;

import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * AbstractInterceptorProvider: Padroniza a criação de interceptores com configuração compilada.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractInterceptorProvider<C> implements InterceptorProvider {

    private final TaskBindingResolver bindingResolver;
    private final Class<C> configClass;

    @Override
    public final Optional<TaskInterceptor> create(FeatureDefinition feature, TaskDefinition taskDef) {
        // A checagem de tipo agora é feita pelo Registry através da busca no mapa do Spring.
        // O Provider apenas executa a criação se chamado.
        CompiledConfiguration<C> compiledConfig = bindingResolver.compile(feature.config(), configClass);
        return Optional.of(createInterceptor(compiledConfig, taskDef));
    }

    protected abstract TaskInterceptor createInterceptor(CompiledConfiguration<C> config, TaskDefinition taskDef);
}
