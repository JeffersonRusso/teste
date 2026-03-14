package br.com.orquestrator.orquestrator.core.engine.interceptor;

import br.com.orquestrator.orquestrator.api.task.InterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.pipeline.compiler.InterceptorStackFactory;
import br.com.orquestrator.orquestrator.core.pipeline.compiler.TaskChainAssembler;
import br.com.orquestrator.orquestrator.domain.model.definition.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * DefaultInterceptorStackFactory: Montador de pilhas dinâmicas e ordenadas.
 * 
 * Implementa o design de elite onde a pilha se auto-organiza baseada nas
 * políticas de cada provedor (Ordem e Globalidade).
 */
@Component
@RequiredArgsConstructor
public class DefaultInterceptorStackFactory implements InterceptorStackFactory {

    private final InterceptorRegistry interceptorRegistry;

    @Override
    public Task assemble(Task core, TaskDefinition def) {
        // 1. Obtém todos os provedores e ordena pela prioridade (Menor = Mais Externo)
        List<TaskInterceptor> chain = interceptorRegistry.getAllProviders()
                .stream()
                .sorted(Comparator.comparingInt(InterceptorProvider::getOrder))
                .map(provider -> resolveInterceptor(provider, def))
                .flatMap(Optional::stream)
                .toList();

        // 2. Monta a cadeia de execução
        return TaskChainAssembler.assemble(core, chain);
    }

    /**
     * Resolve se um interceptor deve ser aplicado a esta task.
     */
    private Optional<TaskInterceptor> resolveInterceptor(InterceptorProvider provider, TaskDefinition def) {
        // Se for global, criamos com uma definição vazia
        if (provider.isGlobal()) {
            return provider.create(new FeatureDefinition(provider.getType(), null), def);
        }

        // Se for dinâmico, verificamos se a task o possui na lista de features
        return def.features().stream()
                .filter(f -> f.type().equalsIgnoreCase(provider.getType()))
                .findFirst()
                .flatMap(f -> provider.create(f, def));
    }
}
