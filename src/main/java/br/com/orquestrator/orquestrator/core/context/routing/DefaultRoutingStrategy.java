package br.com.orquestrator.orquestrator.core.context.routing;

import br.com.orquestrator.orquestrator.core.context.FlowRoutingConfig;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class DefaultRoutingStrategy implements RoutingStrategy {
    @Override
    public Optional<Integer> resolveVersion(FlowRoutingConfig config) {
        return Optional.ofNullable(config.getDefaultVersion());
    }

    @Override
    public int getPriority() { return 99; } // Prioridade baixa (fallback)
}
