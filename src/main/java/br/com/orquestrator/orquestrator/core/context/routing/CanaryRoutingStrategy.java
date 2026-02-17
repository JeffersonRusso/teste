package br.com.orquestrator.orquestrator.core.context.routing;

import br.com.orquestrator.orquestrator.core.context.FlowRoutingConfig;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CanaryRoutingStrategy implements RoutingStrategy {
    @Override
    public Optional<Integer> resolveVersion(FlowRoutingConfig config) {
        if (config.canary() != null && isCanaryActive(config.canary().getPercentage())) {
            return Optional.of(config.canary().getVersion());
        }
        return Optional.empty();
    }

    private boolean isCanaryActive(int percentage) {
        return percentage > 0 && ThreadLocalRandom.current().nextInt(100) < percentage;
    }

    @Override
    public int getPriority() { return 1; }
}
