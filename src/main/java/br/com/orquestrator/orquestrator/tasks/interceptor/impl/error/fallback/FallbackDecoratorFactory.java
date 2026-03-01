package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.fallback;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FallbackDecoratorFactory implements DecoratorFactory<FallbackConfig> {

    @Override
    public String getType() {
        return "FALLBACK";
    }

    @Override
    public Class<FallbackConfig> getConfigClass() {
        return FallbackConfig.class;
    }

    @Override
    public TaskDecorator create(FallbackConfig config, String nodeId) {
        return new FallbackInterceptor(config, nodeId);
    }
}
