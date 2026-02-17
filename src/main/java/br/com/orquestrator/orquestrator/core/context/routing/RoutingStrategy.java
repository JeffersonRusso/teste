package br.com.orquestrator.orquestrator.core.context.routing;

import br.com.orquestrator.orquestrator.core.context.FlowRoutingConfig;
import java.util.Optional;

/**
 * Estratégia para decidir a versão de um fluxo.
 */
public interface RoutingStrategy {
    Optional<Integer> resolveVersion(FlowRoutingConfig config);
    int getPriority();
}
