package br.com.orquestrator.orquestrator.api.task;

import br.com.orquestrator.orquestrator.domain.model.definition.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;

import java.util.Optional;

/**
 * InterceptorProvider: Contrato para provedores de middlewares.
 * O tipo do interceptor é definido pelo nome do Bean do Spring (@Component("TIPO")).
 */
public interface InterceptorProvider {
    /**
     * Cria o interceptor baseado na definição da funcionalidade.
     */
    Optional<TaskInterceptor> create(FeatureDefinition feature, TaskDefinition taskDef);

    // ===================================================================
    // POLÍTICAS DE MONTAGEM (Design de Elite)
    // ===================================================================

    /**
     * Define a ordem de execução na pilha (Menor valor = Mais externo).
     */
    default int getOrder() { return 100; }

    /**
     * Define se este interceptor deve ser aplicado automaticamente a todas as tasks.
     */
    default boolean isGlobal() { return false; }
}
